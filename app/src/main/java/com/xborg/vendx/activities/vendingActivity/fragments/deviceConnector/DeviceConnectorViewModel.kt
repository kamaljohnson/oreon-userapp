package com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Machine
import com.xborg.vendx.preferences.SharedPreference

class DeviceConnectorViewModel: ViewModel() {

    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending
    val selectedMachineNearby = MutableLiveData<Boolean>()

}