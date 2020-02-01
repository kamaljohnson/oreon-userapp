package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.reflect.Type


class ExploreViewModel : ViewModel() {

    var debugText: MutableLiveData<String> = MutableLiveData()

    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    val userLocation = MutableLiveData<Location>()
    val machinesNearby = MutableLiveData<List<Machine>>()
    val selectedMachine = MutableLiveData<Machine>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        selectedMachine.value = Machine()
        debugText.value = "init explorer\n\n"
    }
    
    fun requestNearbyMachines() {
        debugText.value = "request near by machines\n\n"
        Log.i(TAG, "request near by machines")
        val locationDataInJson = Gson().toJson(userLocation.value, Location::class.java)

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .requestNearbyMachinesAsync(location = locationDataInJson, uid = uid)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")
                debugText.value = "Successful to get response: $listResult\n\n"

                val machineListType: Type = object : TypeToken<ArrayList<Machine?>?>() {}.type

                machinesNearby.value = Gson().fromJson(listResult, machineListType)!!
                if(machinesNearby.value!!.isNotEmpty()) {
                    selectNearestMachineToUser()
                } else {    //adding a dummy machine
                    selectedMachine.value = Machine()   //a empty machine constructor creates a dummy machine
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
                debugText.value = "Failed to get response: ${t.message}\n\n"
                apiCallError.value = true
            }
        }
    }

    private fun selectNearestMachineToUser() {
        if(machinesNearby.value!![0].Distance <= 0.1) {
            selectedMachine.value = machinesNearby.value!![0]
        } else {
            selectedMachine.value = Machine(Code = "Dummy")
        }
    }
    fun changeSelectedMachine(machineId: String) {
        if(selectedMachine.value!!.Id != machineId) {
            machinesNearby.value!!.forEach { machine ->
                if(machine.Id == machineId) {
                    Log.i(TAG, "selected machine changed")
                    selectedMachine.value = machine
                    return
                }
            }
        }
    }
}