package com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Machine

enum class DeviceConnectionState {
    None,
    DeviceInfo,
    ScanMode,
    DeviceNearby,
    DeviceNotNearby,
    DeviceBusy,
    DeviceIdle,
    DeviceConnected;

    operator fun compareTo(i: Int): Int {
        return i
    }
}

class DeviceConnectorViewModel: ViewModel() {

    val deviceConnectionState = MutableLiveData<DeviceConnectionState>()

    val deviceScanningMode = MutableLiveData<Boolean>()
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

    init {
        deviceConnectionState.value = DeviceConnectionState.None
    }
}