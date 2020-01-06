package com.xborg.vendx.activities.vendingActivity.fragments.communicators.server

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Bag
import com.xborg.vendx.database.BagStatus
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ServerCommunicatorViewModel : ViewModel() {

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val bag = MutableLiveData<Bag>()

    fun sendEncryptedOtp() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val bagInJson = moshi.adapter(Bag::class.java).toJson(bag.value)!!

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .sendEncryptedOTPAsync(bag = bagInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

//                val tempPayment = moshi.adapter(Bag::class.java).fromJson(listResult)!!

            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response: $e")
            }
        }
    }
}