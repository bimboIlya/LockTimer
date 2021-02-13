package com.example.locktimer2.util

import android.content.Context
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.locktimer2.TimerWorker
import com.example.locktimer2.admin.isAdminActive

fun Context.startDefaultTimer() {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    val defaultDuration = sp.getInt(DEFAULT_TIMER_KEY, -1)
    startTimer(defaultDuration)
}

fun Context.startTimer(duration: Int) {
    if (!isAdminActive()) {
        Toast.makeText(this, "enable admin blease", Toast.LENGTH_SHORT).show()
        return
    }
    if (duration <= 0) return

    val request = OneTimeWorkRequest.Builder(TimerWorker::class.java)
        .setInputData(buildData(duration))
        .addTag(TIMER_LOCK_WORK_NAME)
        .build()

    workManager.enqueueUniqueWork(
        TIMER_LOCK_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        request
    )
}

private fun buildData(input: Int): Data {
    return Data.Builder()
        .putInt(TIMER_LOCK_DURATION_KEY, input)
        .build()
}

fun Context.cancelTimer() {
    workManager.cancelUniqueWork(TIMER_LOCK_WORK_NAME)
}

val Context.workManager get() = WorkManager.getInstance(this)