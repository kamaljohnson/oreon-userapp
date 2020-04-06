package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
import android.util.Log
import android.widget.Toast
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

    var userInventory =  MutableLiveData<List<InventoryItem>>()

    var homeInventoryGroups = MutableLiveData<List<HomeInventoryGroups>>()

    val userDao = UserDatabase.getInstance(application).userDao()

    companion object {
        private var viewModelJob = Job()
        private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

        var selectedMachine = MutableLiveData<Machine>()

        lateinit var context: Application

        fun cartProcessor(cartDao: CartItemDao) {
            if(selectedMachine.value!!.Inventory.isEmpty()) {
                Toast.makeText(context, "No machines selected", Toast.LENGTH_SHORT).show()
                return
            }

            // TODO Recalculate purchase limits
            selectedMachine.value!!.Inventory.forEach { item ->

                val limit = item.Quantity

                ioScope.launch {
                    cartDao.updatePurchaseLimit(item.ItemDetailId, limit)
                }
            }
        }
    }

    init {
        context = application
        selectedMachine.value = Machine()
    }

    fun updateHomeInventoryGroups() {
        if(selectedMachine.value!!.Inventory.isEmpty()) {
            val selectedMachineInventoryGroup = HomeInventoryGroups(
                Title = "Machine",
                Message = "Could'nt find \nMachines near you",
                PaidInventory = false
            )

            val userInventoryGroup = HomeInventoryGroups(
                Title = "Inventory",
                Inventory = userInventory.value!!,
                Message = "",
                PaidInventory = true
            )

            val newHomeInventoryGroups: ArrayList<HomeInventoryGroups> = ArrayList()

            newHomeInventoryGroups.add(selectedMachineInventoryGroup)
            newHomeInventoryGroups.add(userInventoryGroup)

            homeInventoryGroups.value = newHomeInventoryGroups

            return
        }
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
}