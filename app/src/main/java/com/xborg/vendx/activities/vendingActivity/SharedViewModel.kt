package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Machine

class SharedViewModel : ViewModel() {

    val deviceScanningMode = MutableLiveData<Boolean>()
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending
    val selectedMachineNearby = MutableLiveData<Boolean>()
    val selectedMachineConnected = MutableLiveData<Boolean>()
}