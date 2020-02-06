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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.security.MessageDigest

class PaymentStatusViewModel: ViewModel() {

    val order = MutableLiveData<Order>()
    val payment = MutableLiveData<Payment>()

    val paymentState = MutableLiveData<PaymentState>()

    init {
        paymentState.value = PaymentState.None
    }

    fun sendPaymentToken() {

        createPaymentSignature()   //TODO: change this with actual signature

        val paymentDataInJson = Gson().toJson(payment.value, Payment::class.java)

        val paymentsCall = VendxApi.retrofitServices
            .sendPaymentDataAsync(paymentData = paymentDataInJson)
        paymentsCall.enqueue(object : Callback<Payment> {
            override fun onResponse(call: Call<Payment>, response: Response<Payment>) {
                Log.i("Debug", "checkApplicationVersion")
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
                    payment.value = response.body()
                    paymentState.value = PaymentState.PaymentComplete
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<Payment>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(paymentsCall.isCanceled) {
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