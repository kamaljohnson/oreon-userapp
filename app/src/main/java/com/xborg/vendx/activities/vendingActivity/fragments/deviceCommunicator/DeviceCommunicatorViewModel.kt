package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Machine

class DeviceCommunicatorViewModel : ViewModel() {

    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

}