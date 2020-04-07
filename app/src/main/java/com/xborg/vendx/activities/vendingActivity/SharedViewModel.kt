package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScannerState
import com.xborg.vendx.database.machine.Machine
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState
import com.xborg.vendx.database.VendingStatus

class SharedViewModel : ViewModel() {

    val deviceConnectionState = MutableLiveData<DeviceScannerState>()
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

    val vendingState = MutableLiveData<VendingState>()

    val bag = MutableLiveData<Vend>()

    init {
        deviceConnectionState.value = DeviceScannerState.None
        vendingState.value = VendingState.Init
        bag.value = Vend(
//            Uid = uid,
            Status = VendingStatus.Init
        )
    }

    fun sendEncryptedOtpToServer() {

    }

    fun sendEncryptedDeviceLogToServer() {

    }
}