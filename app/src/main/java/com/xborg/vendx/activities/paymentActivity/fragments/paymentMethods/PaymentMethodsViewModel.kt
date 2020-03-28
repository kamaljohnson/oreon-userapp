package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xborg.vendx.database.*
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

private const val TAG = "PaymentMethodsViewModel"

class PaymentMethodsViewModel : ViewModel() {

//    var cartItems = MutableLiveData<List<Item>>()
//
//    val order = MutableLiveData<Order>()
//    val payment = MutableLiveData<Payment>()
//    val paymentState = MutableLiveData<PaymentState>()
//
//    val apiCallError = MutableLiveData<Boolean>()
//
//    init {
//        paymentState.value = PaymentState.None
//    }
//
//    fun calculatePayableAmount() {
//
//        var payableAmount = 0f
//        cartItems.value!!.forEach { item ->
//            if (!item.FromInventory) {
//                payableAmount += item.Cost * item.CartCount
//            }
//        }
//        order.value!!.Amount = payableAmount
//    }
//
//    private fun initPayment(payment: Payment, order: Order) {
//        Log.i(TAG, "payment : " + this.payment.value)
//        if(payment.Id != null) {
//            this.payment.value!!.Id = payment.Id!!
//            this.payment.value!!.Rnd = payment.Rnd!!
//        }
//        this.payment.value!!.OrderId = payment.OrderId
//        this.payment.value!!.Amount = order.Amount
//        this.payment.value!!.Uid = order.Uid
//        this.payment.value!!.Status = PaymentStatus.Init
//
//        paymentState.value = PaymentState.PaymentInit
//        Log.i(TAG, "payment initiated")
//    }
//
//    private fun updateOrder(payment: Payment) {
//        order.value!!.Id = payment.OrderId
//        if(payment.Id != null) {
//            order.value!!.PaymentId = payment.Id!!
//        }
//        Log.i(TAG, "order updated")
//    }
//
//    fun postOrderDetails() {
//
//        Log.i(TAG, "order : " + order.value.toString())
//
//        val orderInJson = Gson().toJson(order.value, Order::class.java)
//
//        val ordersCall = VendxApi.retrofitServices
//            .createOrderAsync(order = orderInJson)
//        ordersCall.enqueue(object : Callback<Payment> {
//            override fun onResponse(call: Call<Payment>, response: Response<Payment>) {
//                Log.i("Debug", "checkApplicationVersion")
//                if(response.code() == 200) {
//                    Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
//                    val tempPayment: Payment? = response.body()
//
//                    updateOrder(
//                        payment = tempPayment!!
//                    )
//                    initPayment (
//                        payment = tempPayment,
//                        order = order.value!!
//                    )
//                } else {
//                    Log.e("Debug", "Failed to get response")
//                    apiCallError.value = true
//                }
//            }
//
//            override fun onFailure(call: Call<Payment>, error: Throwable) {
//                Log.e("Debug", "Failed to get response ${error.message}")
//                apiCallError.value = true
//                if(error is SocketTimeoutException) {
//                    //Connection Timeout
//                    Log.e("Debug", "error type : connectionTimeout")
//                } else if(error is IOException) {
//                    //Timeout
//                    Log.e("Debug", "error type : timeout")
//                } else {
//                    if(ordersCall.isCanceled) {
//                        //Call cancelled forcefully
//                        Log.e("Debug", "error type : cancelledForcefully")
//                    } else {
//                        //generic error handling
//                        Log.e("Debug", "error type : genericError")
//                    }
//                }
//            }
//        })
//    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "destroyed!")
    }
}