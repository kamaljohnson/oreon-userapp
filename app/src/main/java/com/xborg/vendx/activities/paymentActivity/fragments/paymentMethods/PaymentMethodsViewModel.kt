package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemList
import com.xborg.vendx.database.Order
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PaymentMethodsViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    var cartItems = MutableLiveData<List<Item>>()

    private var _payableAmount = MutableLiveData<Float>()
    val payableAmount: LiveData<Float>
        get() = _payableAmount

    private var _order = MutableLiveData<Order>()
    val order: LiveData<Order>
        get() = _order

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        postOrderDetails()
    }

    fun calculatePayableAmount() {

        var payableAmount = 0f
        cartItems.value!!.forEach{item ->
            if(!item.inShelf) {
                payableAmount += item.cost * item.cartCount
            }
        }
        _payableAmount.value = payableAmount
    }

    private fun postOrderDetails() {
        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .createOrderAsync(
                    userId = uid,
                    cart = hashMapOf()
                )
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")
                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                _order.value =
                    moshi.adapter(Order::class.java).fromJson(listResult)!!
                
                Log.i(TAG, "order id: " + order.value!!.id)

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

}