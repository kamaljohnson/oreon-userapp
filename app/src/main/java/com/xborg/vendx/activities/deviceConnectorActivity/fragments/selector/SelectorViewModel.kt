package com.xborg.vendx.activities.deviceConnectorActivity.fragments.selector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites.Permissions

class SelectorViewModel: ViewModel() {

    var currentConnectionModePermissionRequirements =
        MutableLiveData<MutableList<Permissions>>()

    init {
        currentConnectionModePermissionRequirements.value = mutableListOf(
            Permissions.Bluetooth,
            Permissions.BluetoothAdmin
        )
    }
}