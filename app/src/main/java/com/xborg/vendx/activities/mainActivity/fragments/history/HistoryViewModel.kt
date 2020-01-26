package com.xborg.vendx.activities.mainActivity.fragments.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xborg.vendx.database.Transaction
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.reflect.Type


private const val TAG = "HistoryViewModel"

class HistoryViewModel : ViewModel() {
    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    var transactions: MutableLiveData<List<Transaction>> = MutableLiveData()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getTransactions()
    }

    //TODO: combine both items from machine and self to single get req
    private fun getTransactions() {

        coroutineScope.launch {
            val getTransactionsDeferred = VendxApi.retrofitServices.getTransactionsAsync(uid)
            try {
                val listResult = getTransactionsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val transactionListType: Type =
                    object : TypeToken<ArrayList<Transaction?>?>() {}.type

                transactions.value = Gson().fromJson(listResult, transactionListType)

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
                apiCallError.value = true
            }
        }
    }
}