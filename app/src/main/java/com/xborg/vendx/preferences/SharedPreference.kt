package com.xborg.vendx.preferences

import android.content.Context
import android.util.Log

const val TAG = "SharedPreference"

class SharedPreference (context: Context) {
    private val PREFERENCE_NAME = "VendxSharedPreference"
    private val PREFERENCE_SELECTED_MACHINE_MAC = "SelectedMachineMac"

    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getSelectedMachineMac(): String? {
        return preference.getString(PREFERENCE_SELECTED_MACHINE_MAC, "")
    }

    fun setSelectedMachineMac(mac: String) {
        Log.i(TAG, "Mac: $mac added to preferences")
        val editor = preference.edit()
        editor.putString(PREFERENCE_SELECTED_MACHINE_MAC, mac)
        editor.apply()
    }
}