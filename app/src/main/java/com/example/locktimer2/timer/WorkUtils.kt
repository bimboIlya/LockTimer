package com.example.locktimer2.timer

import android.content.Context
import android.service.quicksettings.Tile
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.work.*
import com.example.locktimer2.admin.isAdminActive
import com.example.locktimer2.util.DEFAULT_TIMER_KEY
import com.example.locktimer2.util.TIMER_LOCK_DURATION_KEY
import com.example.locktimer2.util.TIMER_LOCK_WORK_NAME

val Context.workManager get() = WorkManager.getInstance(this)

fun Context.startDefaultTimer() {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    val defaultDuration = preferences.getInt(DEFAULT_TIMER_KEY, -1)
    startTimer(defaultDuration)
}

fun Context.startTimer(duration: Int) {
    if (!isAdminActive()) {
        Toast.makeText(this, "enable admin blease", Toast.LENGTH_SHORT).show()
        return
    }
    if (duration <= 0) return

    val workData = Data.Builder()
        .putInt(TIMER_LOCK_DURATION_KEY, duration)
        .build()

    val request = OneTimeWorkRequest.Builder(TimerWorker::class.java)
        .setInputData(workData)
        .addTag(TIMER_LOCK_WORK_NAME)
        .build()

    workManager.enqueueUniqueWork(
        TIMER_LOCK_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        request
    )
}

fun Context.cancelTimer() {
    workManager.cancelUniqueWork(TIMER_LOCK_WORK_NAME)
}

fun Context.observeTimerWorkStatusForever(block: (Boolean) -> Unit): Observer<List<WorkInfo>> {
    val observer = createWorkObserver(block)
    workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME).observeForever(observer)

    return observer
}

fun Context.removeTimerWorkStatusObserver(observer: Observer<List<WorkInfo>>) {
    workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME).removeObserver(observer)
}

fun Context.observeTimerWorkStatus(lifecycleOwner: LifecycleOwner, block: (Boolean) -> Unit) {
    val observer = createWorkObserver(block)
    workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME).observe(lifecycleOwner, observer)
}

private fun createWorkObserver(block: (Boolean) -> Unit): Observer<List<WorkInfo>> =
    Observer<List<WorkInfo>> { workInfoList ->
        if (workInfoList.isNullOrEmpty()) return@Observer

        val workInfo = workInfoList.first()
        val isTimerRunning = workInfo.state == WorkInfo.State.RUNNING

        block(isTimerRunning)
    }
