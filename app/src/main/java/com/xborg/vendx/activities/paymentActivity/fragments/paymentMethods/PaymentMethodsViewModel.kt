package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "PaymentMethodsViewModel"

class PaymentMethodsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val cartDao = CartItemDatabase.getInstance(application).cartItemDao()
    val cart = MutableLiveData<List<CartItem>>()

    val itemDetailDao = ItemDetailDatabase.getInstance(application).itemDetailDatabaseDao

    val paymentAmount = MutableLiveData<Float>()

    fun calculatePaymentAmount() {

        ioScope.launch {

            var amount = 0f

            cart.value!!.forEach { cartItem ->

                val itemDetail = itemDetailDao.get(cartItem.ItemDetailId)!!

                amount += (itemDetail.Cost - itemDetail.DiscountedCost)

            }

            paymentAmount.postValue(amount)

        }

    }

}