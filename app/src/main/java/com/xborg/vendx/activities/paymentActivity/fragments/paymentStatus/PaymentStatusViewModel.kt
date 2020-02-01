package com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xborg.vendx.database.Order
import com.xborg.vendx.database.Payment
import com.xborg.vendx.database.PaymentState
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.security.MessageDigest

class PaymentStatusViewModel: ViewModel() {

    val order = MutableLiveData<Order>()
    val payment = MutableLiveData<Payment>()

    val paymentState = MutableLiveData<PaymentState>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        paymentState.value = PaymentState.None
    }

    fun sendPaymentToken() {

        createPaymentSignature()   //TODO: change this with actual signature

        val paymentDataInJson = Gson().toJson(payment.value, Payment::class.java)

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .sendPaymentDataAsync(paymentData = paymentDataInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")
                paymentState.value = PaymentState.PaymentPosted
                payment.value =
                    Gson().fromJson(listResult, Payment::class.java)

                paymentState.value = PaymentState.PaymentComplete
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    private fun createPaymentSignature(){

        val paymentId = payment.value!!.Id
        val rnd = payment.value!!.Rnd
        val razorPayPaymentId = payment.value!!.RazorpayPaymentId

        val passToken1 = razorPayPaymentId + paymentId + rnd

        var md = MessageDigest.getInstance("SHA-1")
        var digest = md.digest(passToken1.toByteArray())
        val passToken2 = digest.fold("", { str, it -> str + "%02x".format(it) }) +
                "xeFXq7Qc4QsrCAtOWPRd50aVTBcWFQbL0HviSr6ezfLRCjO8rChpMufwP2XXBNNN"
        md = MessageDigest.getInstance("SHA-1")
        digest = md.digest(passToken2.toByteArray())
        payment.value!!.Signature = digest.fold("", { str, it -> str + "%02x".format(it) })

        paymentState.value = PaymentState.PaymentTokenCreated
    }
}