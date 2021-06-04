package com.example.locktimer2.timer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.locktimer2.R
import com.example.locktimer2.ui.TimerWidget
import com.example.locktimer2.util.ACTION_START_DEFAULT_TIMER
import com.example.locktimer2.util.NOTIFICATION_CHANNEL_ID

class TimerStarterService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, TimerWidget::class.java).setAction(ACTION_START_DEFAULT_TIMER)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.crescent)
            .setContentTitle("Timer starter")
            .setContentText("Click me")
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .build()
    }
}