package com.example.locktimer2.admin

import android.app.Activity
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

private object AdminHelper {

    private lateinit var context: Context
    private lateinit var dpm: DevicePolicyManager
    private lateinit var cm: ComponentName

    fun init(context: Context) {
        this.context = context
        dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        cm = ComponentName(AdminHelper.context, AdminReceiver::class.java)
    }

    fun requestAdmin(context: Context = this.context) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "blease bro")
        }
        context.startActivity(intent)
    }

    // для старого говна на 4.4; оно крашится, тк
    // не может открыть активити из applicationContext
    fun requestAdminCompat(activity: Activity) {
        requestAdmin(activity)
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
}


fun Application.initAdminHelper() = AdminHelper.init(this)

fun requestAdmin() = AdminHelper.requestAdmin()
fun Activity.requestAdminCompat() = AdminHelper.requestAdminCompat(this)
fun Fragment.requestAdminCompat() = requireActivity().requestAdminCompat()

fun removeAdmin() = AdminHelper.removeAdmin()

fun isAdminActive() = AdminHelper.isAdminActive()

fun lockScreen() = AdminHelper.lockScreen()