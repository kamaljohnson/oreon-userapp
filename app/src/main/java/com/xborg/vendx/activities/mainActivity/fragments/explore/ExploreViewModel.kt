package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


class ExploreViewModel : ViewModel() {

    var debugText: MutableLiveData<String> = MutableLiveData()

    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    val userLocation = MutableLiveData<Location>()
    val machinesInZone = MutableLiveData<List<Machine>>()   //machines in 1Km range
    val machineNearby = MutableLiveData<List<Machine>>()    //machines in  vendable range
    val selectedMachine = MutableLiveData<Machine>()        //machine selected for vending

    init {
        selectedMachine.value = Machine()
        debugText.value = "init explorer\n\n"
    }
    
    fun requestMachinesInZone() {
        Log.i(TAG, "request near by machines")
        val locationDataInJson = Gson().toJson(userLocation.value, Location::class.java)

        val nearbyMachinesCall = VendxApi.retrofitServices
            .requestNearbyMachinesAsync(location = locationDataInJson, uid = uid)
        nearbyMachinesCall.enqueue(object : Callback<List<Machine>> {
            override fun onResponse(call: Call<List<Machine>>, response: Response<List<Machine>>) {
                Log.i("Debug", "checkApplicationVersion")
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
                    machinesInZone.value = response.body()
                    if(machinesInZone.value!!.isNotEmpty()) {
                        selectNearestMachineToUser()
                    } else {    //adding a dummy machine
                        selectedMachine.value = Machine()   //a empty machine constructor creates a dummy machine
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                    apiCallError.value = true
                }
            }

            override fun onFailure(call: Call<List<Machine>>, error: Throwable) {
                apiCallError.value = true
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(nearbyMachinesCall.isCanceled) {
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

    private fun selectNearestMachineToUser() {
        if(machinesInZone.value!![0].Distance <= 0.1) {
            selectedMachine.value = machinesInZone.value!![0]
        } else {
            selectedMachine.value = Machine(Code = "Dummy")
        }
    }
    fun changeSelectedMachine(machineId: String) {
        if(selectedMachine.value!!.Id != machineId) {
            machinesInZone.value!!.forEach { machine ->
                if(machine.Id == machineId) {
                    Log.i(TAG, "selected machine changed")
                    selectedMachine.value = machine
                    return
                }
            }
        }
    }
}