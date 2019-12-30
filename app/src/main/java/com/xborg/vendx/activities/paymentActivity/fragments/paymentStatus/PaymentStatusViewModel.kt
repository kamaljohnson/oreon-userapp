package com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.activities.paymentActivity.PaymentStatus
import com.xborg.vendx.database.Order
import com.xborg.vendx.database.Payment
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.MessageDigest

class PaymentStatusViewModel: ViewModel() {

    var paymentData = MutableLiveData<Payment>()
    var paymentStatus = MutableLiveData<PaymentStatus>()
    var order = MutableLiveData<Order>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun sendPaymentToken() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        createPaymentSignature()   //TODO: change this with actual signature

        val paymentDataInJson = moshi.adapter(Payment::class.java).toJson(paymentData.value!!)!!

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .sendPaymentDataAsync(paymentData = paymentDataInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                order.value =
                    moshi.adapter(Order::class.java).fromJson(listResult)!!

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    private fun createPaymentSignature(){

        val paymentId = paymentData.value!!.paymentId
        val orderId = paymentData.value!!.orderId
        val rnd = paymentData.value!!.rnd

        val token = paymentId + orderId + rnd

        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(token.toByteArray())
        paymentData.value!!.signature = digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}