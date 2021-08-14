package com.example.locktimer2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import androidx.viewbinding.BuildConfig
import androidx.work.WorkInfo
import com.example.locktimer2.admin.initAdminHelper
import com.example.locktimer2.timer.TimerStarterService
import com.example.locktimer2.timer.startDefaultTimer
import com.example.locktimer2.timer.workManager
import com.example.locktimer2.util.*
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initAdminHelper()
        observeTimerState()
        createNotificationChannel()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun observeTimerState() {
        workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME).observeForever { workInfoList ->
            if (workInfoList.isNullOrEmpty()) return@observeForever

            val workInfo = workInfoList.first()

            when (workInfo.state) {
                WorkInfo.State.RUNNING -> stopService<TimerStarterService>()

                WorkInfo.State.SUCCEEDED,
                WorkInfo.State.CANCELLED,
                WorkInfo.State.FAILED -> startService<TimerStarterService>()

                else -> Unit
            }
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