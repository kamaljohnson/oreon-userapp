package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.InventoryItem
import com.xborg.vendx.database.User
import com.xborg.vendx.database.UserDao
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "HomeViewModel"

class HomeViewModel(
    val userDatabase: UserDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var userInventory =  MutableLiveData<List<InventoryItem>>()

    init {

    }

}