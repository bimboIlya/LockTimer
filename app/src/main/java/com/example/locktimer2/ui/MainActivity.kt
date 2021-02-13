package com.example.locktimer2.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.work.*
import com.example.locktimer2.R
import com.example.locktimer2.databinding.ActivityMainBinding
import com.example.locktimer2.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setUpClickListeners()

        observeTimerState()
    }

    private fun setUpClickListeners() = with(binding) {
        btnStart.setOnClickListener {
            startTimerIfInputNotPoop()
        }

        btnDefaultStart.setOnClickListener {
            startDefaultTimer()
        }

        btnStop.setOnClickListener {
            cancelTimer()
        }
    }

    private fun startTimerIfInputNotPoop() {
        val input = binding.input.text.toString()
        val numInput: Int = input.toIntOrNull() ?: -1

        if (numInput > 0) {
            startTimer(numInput)
            hideKeyboard()
        } else {
            Toast.makeText(this, "Input is poop", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeTimerState() {
        workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME).observe(this) { workInfoList ->
            if (workInfoList.isNullOrEmpty()) return@observe

            val workInfo = workInfoList.first()
            val isTimerRunning = workInfo.state == WorkInfo.State.RUNNING

            setUiState(isTimerRunning)
        }
    }

    private fun setUiState(isTimerRunning: Boolean) = with(binding) {
        btnStart.isInvisible = isTimerRunning
        input.isInvisible = isTimerRunning
        btnDefaultStart.isInvisible = isTimerRunning
        btnStop.isVisible = isTimerRunning
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> {
                SettingsActivity.start(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
