package com.xborg.vendx.activities.deviceConnectorActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites.Permissions

private const val TAG = "SharedViewModel"

enum class ConnectionSteps(val i : Int) {
    SelectConnectionType(0),
    Prerequisites(1),
    Connection(2)
}

class SharedViewModel: ViewModel() {

    val currentStep = MutableLiveData<ConnectionSteps>()
    var currentConnectionModePermissionRequirements =  MutableLiveData<MutableList<Permissions>>()

    init {
        Log.i(TAG, "here")
        currentStep.value = ConnectionSteps.SelectConnectionType
    }

    fun jumpToNextStep() {
        val nextStep = currentStep.value!!.i + 1
        Log.i(TAG, "nextStep: " + ConnectionSteps.values()[nextStep].toString())
        currentStep.value = ConnectionSteps.values()[nextStep]
        Log.i(TAG, "called jumpToNextStep")
    }
}