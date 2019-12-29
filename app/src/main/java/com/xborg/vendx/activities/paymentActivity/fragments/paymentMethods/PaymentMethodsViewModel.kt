package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.database.Item
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

    private var _orderId = MutableLiveData<String>()
    val orderId: LiveData<String>
        get() = _orderId

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
            val getMachineItemsDeferred = VendxApi.retrofitServices
                .createOrderAsync(
                    userId = uid,
                    cart = hashMapOf()
                )
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult ")
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

}