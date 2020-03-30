package com.xborg.vendx.activities.vendingActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScannerState
import com.xborg.vendx.database.Machine
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState
import com.xborg.vendx.database.VendingStatus
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    val deviceConnectionState = MutableLiveData<DeviceScannerState>()
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

    val vendingState = MutableLiveData<VendingState>()

    val bag = MutableLiveData<Vend>()

    init {
        deviceConnectionState.value = DeviceScannerState.None
        vendingState.value = VendingState.Init
        bag.value = Vend(
            Uid = uid,
            Status = VendingStatus.Init
        )
    }

    fun sendEncryptedOtpToServer() {
//        Log.i(TAG, "sending : " + bag.value + " to server")
//        val bagInJson = Gson().toJson(bag.value, Vend::class.java)
//        val vendsCall = VendxApi.retrofitServices
//            .sendEncryptedOTPAsync(bag = bagInJson)
//        vendsCall.enqueue(object : Callback<Vend> {
//            override fun onResponse(call: Call<Vend>, response: Response<Vend>) {
//                Log.i("Debug", "checkApplicationVersion")
//                if(response.code() == 200) {
//                    Log.i("Debug", "Successful Response code : 200 : homeItems: " + response.body())
//                    val tempBag = response.body()
//                    bag.value!!.EncryptedOtpPlusBag = tempBag!!.EncryptedOtpPlusBag
//                    vendingState.value = VendingState.ReceivedOtpWithBag
//                } else {
//                    Log.e("Debug", "Failed to get response")
//                }
//            }
//
//            override fun onFailure(call: Call<Vend>, error: Throwable) {
//                Log.e("Debug", "Failed to get response ${error.message}")
//                if(error is SocketTimeoutException) {
//                    //Connection Timeout
//                    Log.e("Debug", "error type : connectionTimeout")
//                } else if(error is IOException) {
//                    //Timeout
//                    Log.e("Debug", "error type : timeout")
//                } else {
//                    if(vendsCall.isCanceled) {
//                        //Call cancelled forcefully
//                        Log.e("Debug", "error type : cancelledForcefully")
//                    } else {
//                        //generic error handling
//                        Log.e("Debug", "error type : genericError")
//                    }
//                }
//            }
//        })
    }

    fun sendEncryptedDeviceLogToServer() {
//        val bagInJson = Gson().toJson(bag.value, Vend::class.java)
//        val vendsCall = VendxApi.retrofitServices
//            .sendOnVendCompleteLogAsync(bag = bagInJson, id = bag.value!!.Id)
//        vendsCall.enqueue(object : Callback<Vend> {
//            override fun onResponse(call: Call<Vend>, response: Response<Vend>) {
//                Log.i("Debug", "checkApplicationVersion")
//                if(response.code() == 200) {
//                    Log.i("Debug", "Successful Response code : 200 : homeItems: " + response.body())
//                    val tempBag = response.body()
//                    bag.value!!.EncryptedServerAck = tempBag!!.EncryptedServerAck
//                    vendingState.value = VendingState.ReceivedLogAck
//                } else {
//                    Log.e("Debug", "Failed to get response")
//                }
//            }
//
//            override fun onFailure(call: Call<Vend>, error: Throwable) {
//                Log.e("Debug", "Failed to get response ${error.message}")
//                if(error is SocketTimeoutException) {
//                    //Connection Timeout
//                    Log.e("Debug", "error type : connectionTimeout")
//                } else if(error is IOException) {
//                    //Timeout
//                    Log.e("Debug", "error type : timeout")
//                } else {
//                    if(vendsCall.isCanceled) {
//                        //Call cancelled forcefully
//                        Log.e("Debug", "error type : cancelledForcefully")
//                    } else {
//                        //generic error handling
//                        Log.e("Debug", "error type : genericError")
//                    }
//                }
//            }
//        })
    }
}