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

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "destroyed!")
    }
}