package com.xborg.vendx.preferences

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.xborg.vendx.database.machine.Machine

const val TAG = "SharedPreference"

class SharedPreference (context: Context) {
    private val PREFERENCE_NAME = "VendxSharedPreference"
    private val PREFERENCE_SELECTED_MACHINE = "SelectedMachineMac"

    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getSelectedMachine(): Machine? {
        val gson = Gson()
        val machineJson = preference.getString(PREFERENCE_SELECTED_MACHINE, "")
        return gson.fromJson(machineJson, Machine::class.java)
    }

    fun setSelectedMachine(machine: Machine) {
        Log.i(TAG, "Machine: $machine added to preferences")
        val editor = preference.edit()
        val gson: Gson = Gson()
        val machineJson = gson.toJson(machine)
        editor.putString(PREFERENCE_SELECTED_MACHINE, machineJson)
        editor.apply()
    }
}