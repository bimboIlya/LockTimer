package com.example.locktimer2.ui

import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import com.example.locktimer2.timer.*

class TimerTileService : TileService() {

    private var workObserver: Observer<List<WorkInfo>>? = null

    override fun onStartListening() {
        observeTimerWorkStatusForever { isTimerRunning ->
            qsTile.state = if (isTimerRunning) STATE_ACTIVE else STATE_INACTIVE
            qsTile.updateTile()
        }.also { workObserver = it }
    }

    override fun onStopListening() {
        workObserver
            ?.let(::removeTimerWorkStatusObserver)
            .also { workObserver = null }
    }

    override fun onClick() {
        when (qsTile.state) {
            STATE_INACTIVE -> startDefaultTimer()
            STATE_ACTIVE -> cancelTimer()
            else -> Unit
        }
    }
}
