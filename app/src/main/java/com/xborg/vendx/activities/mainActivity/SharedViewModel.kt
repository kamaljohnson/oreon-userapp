package com.xborg.vendx.activities.mainActivity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.BuildConfig
import com.xborg.vendx.database.*
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.*
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

class SharedViewModel(
    val itemDetailDatabase: ItemDetailDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var versionCode: Int = BuildConfig.VERSION_CODE

    val isInternetAvailable = MutableLiveData<Boolean>()

    var checkedUserLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)
    var getUserLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    var userLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)

    val bluetoothPermission = MutableLiveData<PermissionStatus>()
    val locationPermission = MutableLiveData<PermissionStatus>()
    val locationEnabled = MutableLiveData<Boolean>()

    val userLastLocation = MutableLiveData<Location>()

    init {
        locationPermission.value = PermissionStatus.None
        bluetoothPermission.value = PermissionStatus.None

        initializeItemDetailsDatabase()
    }

    fun addItemToCart(itemId: String): Boolean {
        // TODO: add cartItem to user cart
        return true
    }

    fun removeItemFromCart(itemId: String): Boolean {
        // TODO: remove cartItem from user cart
        return true
    }

    fun resetCart() {
        // TODO: set user cart to ArrayList()
    }

    private fun initializeItemDetailsDatabase() {
        uiScope.launch {
            getAllItemDetailsFromServer()
        }
    }

    private suspend fun getAllItemDetailsFromServer() {
        withContext(Dispatchers.IO) {
            val itemDetailsCall = VendxApi.retrofitServices.getItemDetailsAsync()
            itemDetailsCall.enqueue(object : Callback<List<ItemDetail>> {
                override fun onResponse(call: Call<List<ItemDetail>>, response: Response<List<ItemDetail>>) {
                    if (response.code() == 200) {
                        Log.i("Debug", "Successful Response code : 200")
                        val itemDetails = response.body()
                        if (itemDetails != null) {
                            itemDetailDatabase.clear()
                            itemDetailDatabase.insert(itemDetails)
                        } else {
                            Log.e(TAG, "itemDetails received is null")
                        }
                    } else {
                        Log.e("Debug", "Failed to get response")
                    }
                }

                override fun onFailure(call: Call<List<ItemDetail>>, error: Throwable) {
                    Log.e("Debug", "Failed to get response ${error.message}")
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
    }
}