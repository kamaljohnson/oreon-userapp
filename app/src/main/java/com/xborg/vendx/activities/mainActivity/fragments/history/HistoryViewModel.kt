package com.xborg.vendx.activities.mainActivity.fragments.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Transaction


private const val TAG = "HistoryViewModel"

class HistoryViewModel : ViewModel() {

    val apiCallError = MutableLiveData<Boolean>()

    var transactions: MutableLiveData<List<Transaction>> = MutableLiveData()

    init {
        getTransactions()
    }

    //TODO: combine both homeItems from machine and self to single get req
    fun getTransactions() {

    }
}