package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.machine.Machine
import com.xborg.vendx.database.machine.MachineDatabase
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExploreViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val userLocation = MutableLiveData<Location>()

    val machineDao = MachineDatabase.getInstance(application).machineDao()

    fun getNearbyMachines() {
        val machinesNearbyCall = VendxApi.retrofitServices.getMachinesNearbyAsync()
        machinesNearbyCall.enqueue(object : Callback<List<Machine>> {
            override fun onResponse(call: Call<List<Machine>>, response: Response<List<Machine>>) {
                if (response.code() == 200) {

                    val machines = response.body()

                    ioScope.launch {

                        Log.i("Debug", "machine : $machines")

                        machineDao.insert(machines!!)
                    }

                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<List<Machine>>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

}