package com.example.locktimer2.timer

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.locktimer2.R
import com.example.locktimer2.admin.lockScreen
import com.example.locktimer2.ui.MainActivity
import com.example.locktimer2.util.NOTIFICATION_CHANNEL_ID
import com.example.locktimer2.util.NOTIFICATION_ID
import com.example.locktimer2.util.TIMER_LOCK_DURATION_KEY
import java.text.SimpleDateFormat
import java.util.*

class TimerWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result =
        startTimerForResult()

    private fun startTimerForResult(): Result {
        showStartToast()

        val durationMinutes = inputData.getInt(TIMER_LOCK_DURATION_KEY, -1)
        if (durationMinutes <= 0) return Result.failure()

        showLockNotification(durationMinutes)

        var timeLeftSeconds = durationMinutes * 60

        while (timeLeftSeconds > 0) {
            if (isStopped) return Result.failure()

            Thread.sleep(1000)
            timeLeftSeconds--
        }

        lockScreen()

        return Result.success()
    }

    private fun showStartToast() {
        Handler(context.mainLooper).post {
            Toast.makeText(context, "timer start", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLockNotification(durationMinutes: Int) {
        val triggerDate = Calendar.getInstance(Locale.getDefault())
            .apply { add(Calendar.MINUTE, durationMinutes) }
            .time

        val lockTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(triggerDate)

        setForegroundAsync(getForegroundInfo(lockTime))
    }

    private fun getForegroundInfo(lockTime: String): ForegroundInfo {
        val notification = buildNotification(lockTime)

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(lockTime: String): Notification {
        val cancelPendingIntent = context.workManager.createCancelPendingIntent(id)

        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(context, 42, contentIntent, 0)

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_hourglass)
            .setContentTitle("Timer is running!")
            .setContentText("Screen will be locked at $lockTime")
            .setPriority(PRIORITY_MAX)
            .setShowWhen(false)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "Cancel", cancelPendingIntent)
            .build()
    }
}