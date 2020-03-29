package com.xborg.vendx.activities.mainActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.BuildConfig
import com.xborg.vendx.database.AppInfo
import com.xborg.vendx.database.Location
import com.xborg.vendx.network.VendxApi
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

    var versionCode: Int = BuildConfig.VERSION_CODE

    val isInternetAvailable = MutableLiveData<Boolean>()

    var checkedUserLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)
    var getUserLocation: MutableLiveData<Boolean> = MutableLiveData(false)
    var userLocationAccessed: MutableLiveData<Boolean> = MutableLiveData(false)

    val bluetoothPermission = MutableLiveData<PermissionStatus>()
    val locationPermission = MutableLiveData<PermissionStatus>()
    val locationEnabled = MutableLiveData<Boolean>()

    val userLastLocation = MutableLiveData<Location>()

    var applicationVersionDeprecated = MutableLiveData<Boolean>()
    var applicationAlertMessage = MutableLiveData<String>()


    init {
        locationPermission.value = PermissionStatus.None
        bluetoothPermission.value = PermissionStatus.None

        checkApplicationVersion()
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

    private fun checkApplicationVersion() {
        Log.i(TAG, "checking application version")
        val applicationCall = VendxApi.retrofitServices.getMinimumApplicationVersionAsync()
        applicationCall.enqueue(object : Callback<AppInfo> {
            override fun onResponse(call: Call<AppInfo>, response: Response<AppInfo>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val applicationData = response.body()
                    applicationVersionDeprecated.value = versionCode != applicationData!!.Version
                    applicationAlertMessage.value = applicationData.AlertMessage
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<AppInfo>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
                if (error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if (error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if (applicationCall.isCanceled) {
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