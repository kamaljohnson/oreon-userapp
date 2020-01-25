package com.xborg.vendx.activities.mainActivity.fragments.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Transaction
import com.xborg.vendx.database.TransactionList
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HistoryViewModel"

class HistoryViewModel : ViewModel() {
    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    var transactions: MutableLiveData<List<Transaction>>

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        transactions = MutableLiveData()
        getTransactions()
    }

    //TODO: combine both items from machine and self to single get req
    private fun getTransactions() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        coroutineScope.launch {
            val getTransactionsDeferred = VendxApi.retrofitServices.getTransactionsAsync(uid)
            try {
                val listResult = getTransactionsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                transactions.value =
                    moshi.adapter(TransactionList::class.java).fromJson(listResult)!!.Transactions

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
                apiCallError.value = true
            }
        }
    }
}