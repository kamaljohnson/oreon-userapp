package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item

class PaymentMethodsViewModel : ViewModel() {

    var cartItems = MutableLiveData<List<Item>>()

    private var _payableAmount = MutableLiveData<Float>()
    val payableAmount: LiveData<Float>
        get() = _payableAmount

    fun calculatePayableAmount() {

        var payableAmount = 0f
        cartItems.value!!.forEach{item ->
            if(!item.inShelf) {
                payableAmount += item.cost * item.cartCount
            }
        }
        _payableAmount.value = payableAmount
    }

}