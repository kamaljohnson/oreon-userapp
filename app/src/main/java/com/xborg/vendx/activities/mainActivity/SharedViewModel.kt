package com.xborg.vendx.activities.mainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xborg.vendx.BuildConfig
import com.xborg.vendx.database.Application
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


enum class PermissionStatus {
    None,
    Granted,
    Denied
}

class SharedViewModel : ViewModel() {

    var debugText: MutableLiveData<String> = MutableLiveData()

    var versionCode: Int = BuildConfig.VERSION_CODE

    val isInternetAvailable = MutableLiveData<Boolean>()
    val apiCallError = MutableLiveData<Boolean>()
    val apiCallRetry = MutableLiveData<Boolean>()

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
    var inventoryItems = MutableLiveData<List<Item>>()

    var applicationVersionDeprecated = MutableLiveData<Boolean>()
    var applicationAlertMessage = MutableLiveData<String>()

    val machinesInZone = MutableLiveData<List<Machine>>()   //machines in 1Km range
    val machineNearby = MutableLiveData<List<Machine>>()    //machines in  vendable range

    // [itemId-from, count]     : from -> {Machine, Inventory}
    private var _taggedCartItems = MutableLiveData<MutableMap<String, Int>>()
    val taggedCartItem: LiveData<MutableMap<String, Int>>
        get() = _taggedCartItems

    private var _unTaggedCartItems = mutableMapOf<String, Int>()

    init {
        apiCallError.value = false
        locationPermission.value = PermissionStatus.None
        bluetoothPermission.value = PermissionStatus.None

        _taggedCartItems.value = mutableMapOf()
        debugText.value = "init debugger\n\n"
        checkApplicationVersion()
    }

    fun setMachineItems(machineItems: List<Item>) {
        this.machineItems.value = machineItems
    }

    fun setInventoryItems(inventoryItems: List<Item>) {
        this.inventoryItems.value = inventoryItems
    }

    fun addItemToCart(itemId: String, itemLoc: String): Boolean {
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Inventory/5 etc..

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
            if(item.Id == itemId) {
                return if(item.RemainingInMachine >= unTaggedCount) {

                    tempTaggedCart[sudoItemId] = taggedCount
                    tempUntaggedCart[itemId] = unTaggedCount

                    _taggedCartItems.value = tempTaggedCart
                    _unTaggedCartItems = tempUntaggedCart

                    Log.i(TAG, "unTagged Cart: $_unTaggedCartItems")
                    Log.i(TAG, "remaining in machine : " + (item.RemainingInMachine - unTaggedCount).toString())
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
        val sudoItemId = "$itemLoc/$itemId" //eg Machine/2 , Inventory/5 etc..

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

    fun getInventoryItemsAsJson(): String {
        return getListItemsAsJson(inventoryItems.value!!)
    }

    private fun getListItemsAsJson(items: List<Item>): String {
        return Gson().toJson(items)
    }

    fun resetCart() {
        _taggedCartItems.value = mutableMapOf()
        _unTaggedCartItems = mutableMapOf()
    }

    private fun checkApplicationVersion() {
        Log.i(TAG, "checking application version")
        val applicationCall = VendxApi.retrofitServices.getMinimumApplicationVersionAsync()
        applicationCall.enqueue(object : Callback<Application> {
            override fun onResponse(call: Call<Application>, response: Response<Application>) {
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val applicationData = response.body()
                    applicationVersionDeprecated.value = versionCode != applicationData!!.Version
                    applicationAlertMessage.value = applicationData.AlertMessage
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<Application>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(applicationCall.isCanceled) {
                        //Call cancelled forcefully
                        Log.e("Debug", "error type : cancelledForcefully")
                    } else {
                        //generic error handling
                        Log.e("Debug", "error type : genericError")
                    }
                }
            }
        })
    }
}