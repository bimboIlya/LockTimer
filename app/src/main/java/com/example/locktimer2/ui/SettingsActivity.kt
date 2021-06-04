package com.example.locktimer2.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.locktimer2.R
import com.example.locktimer2.admin.isAdminActive
import com.example.locktimer2.admin.removeAdmin
import com.example.locktimer2.admin.requestAdminCompat
import com.example.locktimer2.util.ADMIN_SWITCH_KEY
import com.example.locktimer2.util.DEFAULT_TIMER_KEY
import com.example.locktimer2.util.setOnClickListener

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var adminSwitch: SwitchPreferenceCompat
    private lateinit var defaultTimeSeekBar: SeekBarPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        initPreferences()
        setPreferencesListeners()
    }

    private fun initPreferences() {
        findPreference<SwitchPreferenceCompat>(ADMIN_SWITCH_KEY)?.let { adminSwitch = it }
        findPreference<SeekBarPreference>(DEFAULT_TIMER_KEY)?.let { defaultTimeSeekBar = it }
    }

    private fun setPreferencesListeners() {
        adminSwitch.setOnClickListener { switch ->
            when (switch.isChecked) {
                true -> requestAdminCompat()
                false -> removeAdmin()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // user may cancel if asked to grant admin, so we make sure to set correct view state
        adminSwitch.isChecked = isAdminActive()
    }
}