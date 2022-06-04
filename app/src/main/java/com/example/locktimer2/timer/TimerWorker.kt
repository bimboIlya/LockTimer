package com.example.locktimer2.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.core.app.NotificationCompat.*
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.locktimer2.R
import com.example.locktimer2.admin.lockScreen
import com.example.locktimer2.ui.TimerWidget
import com.example.locktimer2.util.NOTIFICATION_CHANNEL_ID
import com.example.locktimer2.util.NOTIFICATION_ID
import com.example.locktimer2.util.TIMER_LOCK_DURATION_KEY
import java.text.SimpleDateFormat
import java.util.*

class TimerWorker(
    private val context: Context,
    workerParams: WorkerParameters,
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

    @SuppressLint("LaunchActivityFromNotification")
    private fun buildNotification(lockTime: String): Notification {
        val cancelPendingIntent = context.workManager.createCancelPendingIntent(id)

        val intent = TimerWidget.createIntentForDefaultTimer(context)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)

        return Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.crescent)
            .setContentTitle("Timer is running!")
            .setContentText("Screen will be locked at $lockTime")
            .setPriority(PRIORITY_MAX)
            .setShowWhen(false)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .addAction(0, "Cancel", cancelPendingIntent)
            .build()
    }
}
