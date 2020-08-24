package com.example.locktimer2.util

import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.locktimer2.TimerWorker
import com.example.locktimer2.admin.AdminHelper

fun startTimer(context: Context, duration: Long) {
    if (!AdminHelper.getInstance(context).isAdminActive()) {
        Toast.makeText(context, "enable admin blease", Toast.LENGTH_SHORT).show()
        return
    }
    if (duration <= 0) return

    val request = OneTimeWorkRequest.Builder(TimerWorker::class.java)
        .setInputData(buildData(duration))
        .addTag(TIMER_LOCK_WORK_NAME)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        TIMER_LOCK_WORK_NAME,
        ExistingWorkPolicy.KEEP,
        request
    )
}

private fun buildData(input: Long): Data {
    return Data.Builder()
        .putLong(TIMER_LOCK_DURATION_KEY, input)
        .build()
}

fun startDefaultTimer(context: Context) {
    val sp = PreferenceManager.getDefaultSharedPreferences(context)
    val defaultDuration = sp.getInt(DEFAULT_TIMER_KEY, -1).toLong()
    startTimer(context, defaultDuration)
}

fun cancelTimer(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(TIMER_LOCK_WORK_NAME)
}