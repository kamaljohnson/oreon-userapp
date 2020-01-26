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

class VendingStatusViewModel : ViewModel() {

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val bag = MutableLiveData<Vend>()
    val vendState = MutableLiveData<VendingState>()

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
            val createOrderDeferred = VendxApi.retrofitServices
                .sendOnVendCompleteLogAsync(bag = bagInJson, id = bag.value!!.Id)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val tempBag = Gson().fromJson(listResult, Vend::class.java)
                bag.value!!.EncryptedVendCompleteStatus = tempBag.EncryptedVendCompleteStatus
                vendState.value = VendingState.EncryptedVendStatusReceivedFromServer
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response: $e")
            }
        }
    }
}