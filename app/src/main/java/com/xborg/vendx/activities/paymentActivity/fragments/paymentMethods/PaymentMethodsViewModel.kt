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

    private fun initPayment(
        id: String,
        orderId: String,
        amount: Float,
        rnd: String,
        uid: String
    ) {
        Log.i(TAG, "payment : " + payment.value)
        payment.value!!.Id = id
        payment.value!!.OrderId = orderId
        payment.value!!.Amount = amount
        payment.value!!.Rnd = rnd
        payment.value!!.Uid = uid
        payment.value!!.Status = PaymentStatus.Init

        paymentState.value = PaymentState.PaymentInit
    }

    private fun updateOrder(orderId: String, paymentId: String) {
        order.value!!.Id = orderId
        order.value!!.PaymentId = paymentId
        paymentState.value = PaymentState.OrderIdReceived
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

                val tempPayment = Gson().fromJson(listResult, Payment::class.java)

                Log.i(TAG, "tempPayment: $tempPayment")

                updateOrder(
                    orderId = tempPayment.OrderId,
                    paymentId = tempPayment.Id
                )
                initPayment (
                    id = tempPayment.Id,
                    orderId = tempPayment.OrderId,
                    amount = order.value!!.Amount,
                    rnd = tempPayment.Rnd,
                    uid = order.value!!.Uid
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