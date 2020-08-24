package com.example.locktimer2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.locktimer2.databinding.ActivityMainBinding
import com.example.locktimer2.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val workManager = WorkManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        observeTimerState()

        binding.btnStart.setOnClickListener {
            val input = binding.input.text.toString()
            val numInput: Long = if (input.isNotEmpty()) input.toLong() else -1

            if (numInput > 0) {
                startTimer(this, numInput)
                hideKeyboard()
            } else {
                Toast.makeText(this, "Input is poop", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnStop.setOnClickListener {
            cancelTimer(this)
        }

        binding.btnDefaultStart.setOnClickListener {
            startDefaultTimer(this)
        }
    }

    private fun observeTimerState() {
        workManager.getWorkInfosByTagLiveData(TIMER_LOCK_WORK_NAME)
            .observe(this, Observer { workInfoList ->
                if (workInfoList.isNullOrEmpty()) return@Observer

                val workInfo = workInfoList[0]
                setUiState(workInfo.state == WorkInfo.State.RUNNING)
            })
    }

    private fun setUiState(isTimerRunning: Boolean) {
        if (isTimerRunning) {
            with(binding) {
                btnStart.visibility = View.INVISIBLE
                input.visibility = View.INVISIBLE
                btnDefaultStart.visibility = View.INVISIBLE
                btnStop.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                btnStart.visibility = View.VISIBLE
                input.visibility = View.VISIBLE
                btnDefaultStart.visibility = View.VISIBLE
                btnStop.visibility = View.INVISIBLE
            }
        }
    }

    private fun hideKeyboard() {
        binding.input.clearFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.input.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
