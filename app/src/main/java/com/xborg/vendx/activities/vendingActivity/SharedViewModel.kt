package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScannerState
import com.xborg.vendx.database.Machine

enum class VendingState {

    //ble connection states
    Init,
    DeviceDiscovered,
    ConnectionRequest,
    Connecting,
    Connected,

    //vending states
    ReceivedOtp,
    SendOtpWithBag,
    Vending,
    VendingDone,
    VendingComplete,
    ReceivedLog,
    SendLogAck,

    //error states
    Error,
}

class SharedViewModel : ViewModel() {

    val deviceConnectionState = MutableLiveData<DeviceScannerState>()
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

    val vendingState = MutableLiveData<VendingState>()

    init {
        deviceConnectionState.value = DeviceScannerState.None
        vendingState.value = VendingState.Init
    }

}