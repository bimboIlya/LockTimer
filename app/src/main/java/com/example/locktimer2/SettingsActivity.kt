package com.example.locktimer2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.locktimer2.admin.AdminHelper
import com.example.locktimer2.util.ADMIN_SWITCH_KEY
import com.example.locktimer2.util.DEFAULT_TIMER_KEY

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var adminHelper: AdminHelper

        private lateinit var adminSwitch: SwitchPreferenceCompat
        private lateinit var defaultTimeSeekBar: SeekBarPreference


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            adminHelper = AdminHelper.getInstance(requireContext())
            initPreferences()
            handlePreferences()
        }

        private fun initPreferences() {
            findPreference<SwitchPreferenceCompat>(ADMIN_SWITCH_KEY)?.let { adminSwitch = it }
            findPreference<SeekBarPreference>(DEFAULT_TIMER_KEY)?.let { defaultTimeSeekBar = it }
        }

        private fun handlePreferences() {
            adminSwitch.setOnPreferenceClickListener {
                it as SwitchPreferenceCompat
                if (it.isChecked) {
                    adminHelper.requestAdmin()
                } else {
                    adminHelper.removeAdmin()
                }
                true
            }
        }

        override fun onResume() {
            super.onResume()
            checkIfAdminGranted()
        }

        // user may cancel if asked to grant admin, so we make sure to set correct view state
        private fun checkIfAdminGranted() {
            adminSwitch.isChecked = adminHelper.isAdminActive()
        }
    }
}