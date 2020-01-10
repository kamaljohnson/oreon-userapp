package com.xborg.vendx.activities.vendingActivity.fragments.status

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
    val bagStatus = MutableLiveData<VendingState>()

    init {
        bagStatus.value = VendingState.None
    }

    fun sendEncryptedOtp() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val bagInJson = moshi.adapter(Vend::class.java).toJson(bag.value)!!

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .sendEncryptedOTPAsync(bag = bagInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val tempBag = moshi.adapter(Vend::class.java).fromJson(listResult)!!
                bag.value!!.encryptedOtpPlusBag = tempBag.encryptedOtpPlusBag
                bagStatus.value = VendingState.EncryptedOtpPlusBagReceived

            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response: $e")
            }
        }
    }
}