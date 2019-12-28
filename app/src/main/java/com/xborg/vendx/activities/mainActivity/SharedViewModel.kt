package com.xborg.vendx.activities.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item

class SharedViewModel : ViewModel() {

    var machineItems = MutableLiveData<List<Item>>()
    var shelfItems = MutableLiveData<List<Item>>()

    // [itemId-from, count]     : from -> {Machine, Shelf}
    private var _cartItems = MutableLiveData<MutableMap<String, Int>>()
    val cartItem: LiveData<MutableMap<String, Int>>
        get() = _cartItems

    init {
        _cartItems.value = mutableMapOf()
    }

    fun setMachineItems(machineItems: List<Item>) {
        this.machineItems.value = machineItems
    }

    fun setShelfItems(shelfItems: List<Item>) {
        this.shelfItems.value = shelfItems
    }

    fun addItemToCart(itemId: String, itemLoc: String) {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        val tempCart = _cartItems.value

        if (tempCart!!.containsKey(sudoItemId)) {
            val count = tempCart[sudoItemId]!! + 1
            tempCart[sudoItemId] = count
        } else {
            tempCart[sudoItemId] = 1
        }

        _cartItems.value = tempCart
    }

    fun removeItemFromCart(itemId: String, itemLoc: String) {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        val tempCart = _cartItems.value

        if (tempCart!!.containsKey(sudoItemId)) {
            val count = tempCart[sudoItemId]!! - 1
            tempCart[sudoItemId] = count
            if (tempCart[sudoItemId] == 0) {
                tempCart.remove(sudoItemId)
            }
        } else {
            // this block should not be called
        }

        _cartItems.value = tempCart
    }
}