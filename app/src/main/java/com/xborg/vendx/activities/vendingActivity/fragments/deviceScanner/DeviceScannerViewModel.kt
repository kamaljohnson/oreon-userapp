package com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class DeviceScannerState {
    None,
    DeviceInfo,
    ScanMode,
    DeviceNearby,
    DeviceNotNearby,
    DeviceBusy,
    DeviceIdle;

    operator fun compareTo(i: Int): Int {
        return i
    }
}

class DeviceScannerViewModel: ViewModel() {

    private val deviceConnectionState = MutableLiveData<DeviceScannerState>()

    init {
        deviceConnectionState.value = DeviceScannerState.None
    }
}