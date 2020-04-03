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

    val apiCallError = MutableLiveData<Boolean>()

    init {
        paymentState.value = PaymentState.None
    }

    fun sendPaymentToken() {

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