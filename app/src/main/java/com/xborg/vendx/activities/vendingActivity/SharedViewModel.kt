package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Machine

class SharedViewModel : ViewModel() {

    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending
    val selectedMachineNearby = MutableLiveData<Boolean>()
}