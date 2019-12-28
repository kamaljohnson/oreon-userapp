package com.xborg.vendx.activities.paymentActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import java.io.Serializable
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

    fun setCartItems(cartItemsAsHash: Serializable) {
        _cartItems.value = cartItemsAsHash as MutableMap<String, Int>
    }

    fun setMachineItems(machineItemsAsJson: Serializable) {
        shelfItems.value = convertJsonToItemList(machineItemsAsJson as String)
    }

    fun setShelfItems(shelfItemsAsJson: Serializable) {
        shelfItems.value = convertJsonToItemList(shelfItemsAsJson as String)
    }

    private fun convertJsonToItemList(json: String): List<Item> {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val itemListDataType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Item::class.java
        )
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(itemListDataType)

        return adapter.fromJson(json)!!
    }

}