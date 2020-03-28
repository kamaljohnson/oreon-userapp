package com.xborg.vendx.activities.mainActivity.fragments.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.database.Transaction
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


private const val TAG = "HistoryViewModel"

class HistoryViewModel : ViewModel() {
    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    var transactions: MutableLiveData<List<Transaction>> = MutableLiveData()

    init {
        getTransactions()
    }

    //TODO: combine both homeItems from machine and self to single get req
    fun getTransactions() {

        val transactionsCall = VendxApi.retrofitServices.getTransactionsAsync(uid)
        transactionsCall.enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200 : homeItems: " + response.body())
                    transactions.value = response.body()
                } else {
                    Log.e("Debug", "Failed to get response")
                    apiCallError.value = true
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, error: Throwable) {
                apiCallError.value = true
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(transactionsCall.isCanceled) {
                        //Call cancelled forcefully
                        Log.e("Debug", "error type : cancelledForcefully")
                    } else {
                        //generic error handling
                        Log.e("Debug", "error type : genericError")
                    }
                }
            }
        })
    }
}