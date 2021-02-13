package com.example.locktimer2.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        view.clearFocus()
        val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Fragment.hideKeyboard() = requireActivity().hideKeyboard()


inline fun <reified P : Preference> P.setOnClickListener(
    crossinline clickListener: (P) -> Unit
) = setOnPreferenceClickListener {
    clickListener(this)
    true
}