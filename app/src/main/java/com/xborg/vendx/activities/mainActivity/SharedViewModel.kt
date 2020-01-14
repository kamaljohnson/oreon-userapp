package com.xborg.vendx.activities.mainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.Machine
import java.lang.reflect.Type

enum class PermissionStatus {
    None,
    Granted,
    Denied
}

class SharedViewModel : ViewModel() {

    val isInternetAvailable = MutableLiveData<Boolean>()

    var checkedUserLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)
    var getUserLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    var userLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)

    val bluetoothPermission = MutableLiveData<PermissionStatus>()
    val locationPermission = MutableLiveData<PermissionStatus>()
    val locationEnabled = MutableLiveData<Boolean>()

    val userLastLocation = MutableLiveData<Location>()

    val selectedMachine = MutableLiveData<Machine>()
    val selectedMachineLoaded = MutableLiveData<Boolean>()

    var machineItems = MutableLiveData<List<Item>>()
    var shelfItems = MutableLiveData<List<Item>>()

    // [itemId-from, count]     : from -> {Machine, Shelf}
    private var _taggedCartItems = MutableLiveData<MutableMap<String, Int>>()
    val taggedCartItem: LiveData<MutableMap<String, Int>>
        get() = _taggedCartItems

    private var _unTaggedCartItems = mutableMapOf<String, Int>()

    init {
        locationPermission.value = PermissionStatus.None
        bluetoothPermission.value = PermissionStatus.None

        _taggedCartItems.value = mutableMapOf()
    }

    fun setMachineItems(machineItems: List<Item>) {
        this.machineItems.value = machineItems
    }

    fun setShelfItems(shelfItems: List<Item>) {
        this.shelfItems.value = shelfItems
    }

    fun addItemToCart(itemId: String, itemLoc: String): Boolean {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        val tempTaggedCart = _taggedCartItems.value
        val tempUntaggedCart = _unTaggedCartItems

        val taggedCount: Int
        val unTaggedCount: Int

        taggedCount = if (tempTaggedCart!!.containsKey(sudoItemId)) {
            tempTaggedCart[sudoItemId]!! + 1
        } else {
            1
        }

        unTaggedCount = if(tempUntaggedCart!!.containsKey(itemId)) {
            tempUntaggedCart[itemId]!! + 1
        } else {
            1
        }

        //TODO: change code while handling cart from shop
        //checking if item purchase limit reached if item in machine
        machineItems.value!!.forEach{item ->
            if(item.id == itemId) {
                return if(item.remainingInMachine >= unTaggedCount) {

                    tempTaggedCart[sudoItemId] = taggedCount
                    tempUntaggedCart[itemId] = unTaggedCount

                    _taggedCartItems.value = tempTaggedCart
                    _unTaggedCartItems = tempUntaggedCart

                    Log.i(TAG, "unTagged Cart: $_unTaggedCartItems")
                    Log.i(TAG, "remaining in machine : " + (item.remainingInMachine - unTaggedCount).toString())
                    Log.i(TAG, "item can be added to cart")
                    true
                } else {
                    Log.i(TAG, "item can'nt be added to cart")
                    false
                }
            }
        }
        Log.i(TAG, "item can'nt be added to cart")
        return false
    }

    fun removeItemFromCart(itemId: String, itemLoc: String) : Boolean{
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Shelf/5 etc..

        val tempTaggedCart = _taggedCartItems.value
        val tempUnTaggedCart = _unTaggedCartItems

        if (tempTaggedCart!!.containsKey(sudoItemId) && tempUnTaggedCart.containsKey(itemId)) {
            val taggedCount = tempTaggedCart[sudoItemId]!! - 1
            val unTaggedCount = tempUnTaggedCart[itemId]!! - 1
            tempTaggedCart[sudoItemId] = taggedCount
            tempUnTaggedCart[itemId] = unTaggedCount
            if (tempTaggedCart[sudoItemId] == 0) {
                tempTaggedCart.remove(sudoItemId)
            }
            if (tempUnTaggedCart[itemId] == 0) {
                tempUnTaggedCart.remove(itemId)
            }
        } else {
            // this block should not be called
        }

        _taggedCartItems.value = tempTaggedCart
        return true
    }

    fun getCartItemsAsPassable(): HashMap<String, Int> {
        return taggedCartItem.value as HashMap<String, Int>
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