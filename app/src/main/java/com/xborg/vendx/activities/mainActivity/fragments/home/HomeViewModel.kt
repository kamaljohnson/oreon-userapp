package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.*
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
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    var userInventory =  MutableLiveData<List<InventoryItem>>()

    var selectedMachine = MutableLiveData<Machine>()

    private val userDao = UserDatabase.getInstance(application).userDao()

    init {

    }

    private fun getMachineData(machine_id: String) {
        val itemDetailsCall = VendxApi.retrofitServices.getMachineAsync(machine_id)
        itemDetailsCall.enqueue(object : Callback<Machine> {
            override fun onResponse(call: Call<Machine>, response: Response<Machine>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val machine = response.body()
                    if (machine != null) {
                        selectedMachine.value = machine
                        ioScope.launch {

                        }
                    } else {
                        Log.e("Debug", "machine received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<Machine>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    private fun updateUserInventory() {
        ioScope.launch {
            userInventory.value = userDao.get(1)!!.Inventory
        }
    }

}