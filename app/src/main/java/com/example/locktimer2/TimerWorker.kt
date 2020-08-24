package com.example.locktimer2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.locktimer2.admin.AdminHelper
import com.example.locktimer2.util.NOTIFICATION_ID
import com.example.locktimer2.util.TIMER_LOCK_DURATION_KEY
import java.text.SimpleDateFormat
import java.util.*

class TimerWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var durationMinutes: Long = -1

    override fun doWork(): Result {
        durationMinutes = inputData.getLong(TIMER_LOCK_DURATION_KEY, -1)
        if (durationMinutes <= 0) return Result.failure()

        val triggerDate = Calendar.getInstance(Locale.getDefault())
            .apply { add(Calendar.MINUTE, durationMinutes.toInt()) }.time

        val lockTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(triggerDate)

        setForegroundAsync(getForegroundInfo(lockTime))

        while (durationMinutes > 0) {
            Thread.sleep(60 * 1000)  // 1 minute
            durationMinutes -= 1
        }
        AdminHelper.getInstance(context).lockScreen()

        return Result.success()
    }

    private fun getForegroundInfo(lockTime: String): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = buildNotification(lockTime)

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(lockTime: String): Notification {
        val cancelPendingIntent = WorkManager.getInstance(context).createCancelPendingIntent(id)

        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(context, 42, contentIntent, 0)

        return NotificationCompat.Builder(context, NOTIFICATION_ID.toString())
            .setSmallIcon(R.drawable.icon_hourglass)
            .setContentTitle("Timer is running!")
            .setContentText("Screen will be locked at $lockTime")
            .setShowWhen(false)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Cancel", cancelPendingIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel("id", "name", NotificationManager.IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(channel)
    }
}