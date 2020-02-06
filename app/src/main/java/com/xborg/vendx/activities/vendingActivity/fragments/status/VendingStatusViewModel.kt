package com.xborg.vendx.activities.vendingActivity.fragments.status

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState
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

class VendingStatusViewModel : ViewModel() {

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val bag = MutableLiveData<Vend>()
    val vendState = MutableLiveData<VendingState>()
    val retryDeviceConnection = MutableLiveData<Boolean>()

    init {
        vendState.value = VendingState.Init
    }

    fun sendEncryptedOtpToServer() {
        val bagInJson = Gson().toJson(bag.value, Vend::class.java)
        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .sendEncryptedOTPAsync(bag = bagInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val tempBag = Gson().fromJson(listResult, Vend::class.java)
                bag.value!!.Id = tempBag.Id
                bag.value!!.EncryptedOtpPlusBag = tempBag.EncryptedOtpPlusBag
                vendState.value = VendingState.EncryptedOtpPlusBagReceivedFromServer
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response: $e")
            }
        }
    }

    fun sendEncryptedDeviceLogToServer() {
        val bagInJson = Gson().toJson(bag.value, Vend::class.java)
        coroutineScope.launch {
            val vendsCall = VendxApi.retrofitServices
                .sendOnVendCompleteLogAsync(bag = bagInJson, id = bag.value!!.Id)
            vendsCall.enqueue(object : Callback<Vend> {
                override fun onResponse(call: Call<Vend>, response: Response<Vend>) {
                    Log.i("Debug", "checkApplicationVersion")
                    if(response.code() == 200) {
                        Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
                        val tempBag = response.body()
                        bag.value!!.EncryptedVendCompleteStatus = tempBag!!.EncryptedVendCompleteStatus
                        vendState.value = VendingState.EncryptedVendStatusReceivedFromServer
                    } else {
                        Log.e("Debug", "Failed to get response")
                    }
                }

                override fun onFailure(call: Call<Vend>, error: Throwable) {
                    Log.e("Debug", "Failed to get response ${error.message}")
                    if(error is SocketTimeoutException) {
                        //Connection Timeout
                        Log.e("Debug", "error type : connectionTimeout")
                    } else if(error is IOException) {
                        //Timeout
                        Log.e("Debug", "error type : timeout")
                    } else {
                        if(vendsCall.isCanceled) {
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

    fun sendCancelVendRequestToServer() {
        //TODO: handle this
    }
}