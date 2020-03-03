package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector.DeviceConnectionState
import com.xborg.vendx.database.Machine

class SharedViewModel : ViewModel() {

    val deviceConnectionState = MutableLiveData<DeviceConnectionState>()

    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending
}