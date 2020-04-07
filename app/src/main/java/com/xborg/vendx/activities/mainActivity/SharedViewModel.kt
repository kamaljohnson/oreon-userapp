package com.xborg.vendx.activities.mainActivity

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.BuildConfig
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeViewModel
import com.xborg.vendx.database.*
import com.xborg.vendx.database.AccessTokenDatabase
import com.xborg.vendx.database.user.User
import com.xborg.vendx.database.user.UserDatabase
import com.xborg.vendx.network.VendxAPIService
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


enum class PermissionStatus {
    None,
    Granted,
    Denied
}

class SharedViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var versionCode: Int = BuildConfig.VERSION_CODE

    val isInternetAvailable = MutableLiveData<Boolean>()

    var checkedUserLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)
    var getUserLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    var userLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)

    val bluetoothPermission = MutableLiveData<PermissionStatus>()
    val locationPermission = MutableLiveData<PermissionStatus>()
    val locationEnabled = MutableLiveData<Boolean>()

    val userLastLocation = MutableLiveData<Location>()

    val itemDetailDao = ItemDetailDatabase.getInstance(application).itemDetailDatabaseDao
    val userDao = UserDatabase.getInstance(application).userDao()
    val accessTokenDao = AccessTokenDatabase.getInstance(application).accessTokenDao()

    val cartDao = CartItemDatabase.getInstance(application).cartItemDao()
    val cart = MutableLiveData<List<CartItem>>()
    private var cartContext = CartContext.None

    init {
        locationPermission.value = PermissionStatus.None
        bluetoothPermission.value = PermissionStatus.None

        ioScope.launch {
            VendxAPIService.accessToken = "token " + accessTokenDao.getToken()
            initializeItemDetailsDatabase()
        }
    }

    fun processCart() {
        if(cartContext == CartContext.None) {
            // TODO Check which is the current context
            cartContext = CartContext.Machine
        }

        if(cart.value!!.isEmpty())
            return

        when(cartContext) {
            CartContext.Machine -> {
                HomeViewModel.cartProcessor(cartDao)
            }
            CartContext.Shop -> {
                TODO()
            }
        }

    }

    fun resetCart() {
        ioScope.launch {
            cartDao.reset()
        }
    }

    private fun initializePrerequisiteDatabase() {

        initializeItemDetailsDatabase()
    }

    private fun initializeItemDetailsDatabase() {
        val itemDetailsCall = VendxApi.retrofitServices.getItemDetailsAsync()
        itemDetailsCall.enqueue(object : Callback<List<ItemDetail>> {
            override fun onResponse(call: Call<List<ItemDetail>>, response: Response<List<ItemDetail>>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val itemDetails = response.body()
                    if (itemDetails != null) {
                        ioScope.launch {
                            itemDetailDao.insert(itemDetails)
                            initializeUserDatabase()
                        }
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

    fun initializeUserDatabase() {
        val userCall = VendxApi.retrofitServices.getUserInfoAsync()
        userCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val user = response.body()
                    if (user != null) {
                        ioScope.launch {
                            Log.i("Debug", "user : $user")
                            userDao.insert(user)
                            getUserLocation.postValue(true)
                        }
                    } else {
                        Log.e("Debug", "user info received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<User>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    override fun onCleared() {
        super.onCleared()

        viewModelJob.cancel()
    }
}