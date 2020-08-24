package com.example.locktimer2.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import timber.log.Timber


class AdminHelper private constructor (private val context: Context) {

    private val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val cm = ComponentName(context, AdminReceiver::class.java)

    fun requestAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "blease bro")
        context.startActivity(intent)
    }

    fun removeAdmin() {
        dpm.removeActiveAdmin(cm)
    }

    fun isAdminActive(): Boolean {
        return dpm.isAdminActive(cm)
    }

    fun lockScreen() {
        if (isAdminActive()) dpm.lockNow()
    }

    companion object {

        @Volatile private var instance: AdminHelper? = null

        // potential memory leak because of activity context instead of app context
        // made this way so app could ask for admin permission (wouldn't launch action otherwise)
        // true for Android 4.4, works fine on 7.1.2
        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: AdminHelper(context).also { instance = it }  // appContext
            }
    }
}