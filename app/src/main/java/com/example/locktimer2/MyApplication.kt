package com.example.locktimer2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.viewbinding.BuildConfig
import com.example.locktimer2.admin.initAdminHelper
import com.example.locktimer2.util.NOTIFICATION_CHANNEL_ID
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initAdminHelper()
        createNotificationChannel()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
