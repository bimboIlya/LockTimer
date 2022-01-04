package com.example.locktimer2.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
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

    @SuppressLint("LaunchActivityFromNotification")
    private fun createNotification(): Notification {
        val intent = TimerWidget.createIntentForDefaultTimer(this)
        val flag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flag)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.crescent)
            .setContentTitle("Timer starter")
            .setContentText("Click me")
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setShowWhen(false)
            .build()
    }
}