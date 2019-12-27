package com.xborg.vendx.activities.mainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item

class SharedViewModel : ViewModel(){

    var machineItems = MutableLiveData<List<Item>>()
    var shelfItems = MutableLiveData<List<Item>>()

    var cartItems = MutableLiveData<List<Map<Item, Int>>>()

    fun setMachineItems(machineItems:List<Item>){
        this.machineItems.value = machineItems
    }
    fun setShelfItems(shelfItems:List<Item>){
        this.shelfItems.value = shelfItems
    }
}