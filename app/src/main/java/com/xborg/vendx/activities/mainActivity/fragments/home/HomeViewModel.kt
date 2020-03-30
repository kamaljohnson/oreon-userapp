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

        initializeUserDatabase()
    }

    private fun initializeUserDatabase() {
        // TODO: pass the actual user id
        val userCall = VendxApi.retrofitServices.getUserInfoAsync("1")
        userCall.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val user = response.body()
                    if (user != null) {
                        ioScope.launch {
                            userInventory.value = user.Inventory

                            userDatabase.clear()
                            userDatabase.insert(user)
                        }
                    } else {
                        Log.e(com.xborg.vendx.activities.mainActivity.TAG, "user info received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<User>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }
}