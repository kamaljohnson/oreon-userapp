package com.xborg.vendx.activities.mainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item

class SharedViewModel : ViewModel(){

    var machineItems = MutableLiveData<List<Item>>()
    var shelfItems = MutableLiveData<List<Item>>()

    // [itemId-from, count]     : from -> {Machine, Shelf}
    var cartItems = MutableLiveData<MutableMap<String, Int>>()

    init {
        cartItems.value = mutableMapOf()
    }

    fun setMachineItems(machineItems:List<Item>){
        this.machineItems.value = machineItems
    }
    fun setShelfItems(shelfItems:List<Item>){
        this.shelfItems.value = shelfItems
    }

    fun addItemToCart(itemId: String, itemLoc: String) {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        if(cartItems.value!!.containsKey(sudoItemId)) {
            val count = cartItems.value!![sudoItemId]!! + 1
            cartItems.value!![sudoItemId] = count
        } else {
            cartItems.value!![sudoItemId] = 1
        }
    }
    fun removeItemFromCart(itemId: String, itemLoc: String) {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        if(cartItems.value!!.containsKey(sudoItemId)) {
            val count = cartItems.value!![sudoItemId]!! - 1
            cartItems.value!![sudoItemId] = count
            if(cartItems.value!![sudoItemId] == 0) {
                cartItems.value!!.remove(sudoItemId)
            }
        } else {
            // this block should not be called
        }
    }
}