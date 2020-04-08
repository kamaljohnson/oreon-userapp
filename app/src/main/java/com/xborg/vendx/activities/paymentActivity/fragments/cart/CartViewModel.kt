package com.xborg.vendx.activities.paymentActivity.fragments.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.CartItem
import com.xborg.vendx.database.CartItemDatabase

class CartViewModel(
   application: Application
) : AndroidViewModel(application) {

    val cartDao = CartItemDatabase.getInstance(application).cartItemDao()
    val cart = MutableLiveData<List<CartItem>>()

}