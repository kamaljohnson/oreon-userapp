package com.xborg.vendx.activities.deviceConnectorActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites.Permissions

enum class ConnectionSteps {
    SelectConnectionType,
    Prerequisites,
    Connection
}

class SharedViewModel: ViewModel() {

    val currentStep = MutableLiveData<ConnectionSteps>()
    var currentConnectionModePermissionRequirements =  MutableLiveData<MutableList<Permissions>>()

    init {
        currentStep.value = ConnectionSteps.SelectConnectionType
    }

    fun jumpToNextStep() {
        val nextStep = currentStep.value as Int + 1
        currentStep.value = nextStep as ConnectionSteps
    }
}