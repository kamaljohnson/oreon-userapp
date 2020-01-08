package com.xborg.vendx.activities.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import java.lang.reflect.Type


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

    fun addItemToCart(itemId: String, itemLoc: String): Boolean {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        val tempCart = _cartItems.value

        if (tempCart!!.containsKey(sudoItemId)) {
            val count = tempCart[sudoItemId]!! + 1
            tempCart[sudoItemId] = count
        } else {
            tempCart[sudoItemId] = 1
        }

        _cartItems.value = tempCart
        return true
    }

    fun removeItemFromCart(itemId: String, itemLoc: String) : Boolean{
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
        return true
    }

    fun getCartItemsAsPassable(): HashMap<String, Int> {
        return cartItem.value as HashMap<String, Int>
    }

    fun getMachineItemsAsJson(): String {
        return getListItemsAsJson(machineItems.value!!)
    }

    fun getShelfItemsAsJson(): String {
        return getListItemsAsJson(shelfItems.value!!)
    }

    private fun getListItemsAsJson(items: List<Item>): String {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val itemListDataType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Item::class.java
        )
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(itemListDataType)

        return adapter.toJson(items)!!
    }
}