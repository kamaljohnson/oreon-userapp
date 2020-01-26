package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.xborg.vendx.database.*
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "PaymentMethodsViewModel"

class PaymentMethodsViewModel : ViewModel() {

    var cartItems = MutableLiveData<List<Item>>()

    val order = MutableLiveData<Order>()
    val payment = MutableLiveData<Payment>()

    val paymentState = MutableLiveData<PaymentState>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        paymentState.value = PaymentState.None
    }

    fun calculatePayableAmount() {

        var payableAmount = 0f
        cartItems.value!!.forEach { item ->
            if (!item.InShelf) {
                payableAmount += item.Cost * item.cartCount
            }
        }
        order.value!!.Amount = payableAmount
    }

    private fun initPayment(payment: Payment, order: Order) {
        Log.i(TAG, "payment : " + this.payment.value)
        if(payment.Id != null) {
            this.payment.value!!.Id = payment.Id!!
            this.payment.value!!.Rnd = payment.Rnd!!
        }
        this.payment.value!!.OrderId = payment.OrderId
        this.payment.value!!.Amount = order.Amount
        this.payment.value!!.Uid = order.Uid
        this.payment.value!!.Status = PaymentStatus.Init

        paymentState.value = PaymentState.PaymentInit
        Log.i(TAG, "payment initiated")
    }

    private fun updateOrder(payment: Payment) {
        order.value!!.Id = payment.OrderId
        if(payment.Id != null) {
            order.value!!.PaymentId = payment.Id!!
        }
        Log.i(TAG, "order updated")
    }

    fun postOrderDetails() {

        Log.i(TAG, "order : " + order.value.toString())

        val orderInJson = Gson().toJson(order.value, Order::class.java)

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .createOrderAsync(order = orderInJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val tempPayment: Payment
                tempPayment = Gson().fromJson(listResult, Payment::class.java)

                updateOrder(
                    payment = tempPayment
                )
                initPayment (
                    payment = tempPayment,
                    order = order.value!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response: $e")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "destroyed!")
        viewModelJob.cancel()
    }
}