package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScannerState
import com.xborg.vendx.database.Machine

class SharedViewModel : ViewModel() {

    val deviceConnectionState = MutableLiveData<DeviceScannerState>()

    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending
}