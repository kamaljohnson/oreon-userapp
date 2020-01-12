package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Location
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ExploreViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    val userLocation = MutableLiveData<Location>()

    val machinesNearby = MutableLiveData<List<Machine>>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun requestNearbyMachines() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val locationDataInJson = moshi.adapter(Location::class.java).toJson(userLocation.value!!)!!

        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .requestNearbyMachinesAsync(location = locationDataInJson, uid = uid)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val machineListType =
                    Types.newParameterizedType(List::class.java, Machine::class.java)
                val adapter: JsonAdapter<List<Machine>> = moshi.adapter(machineListType)

                machinesNearby.value = adapter.fromJson(listResult)!!

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }
}
