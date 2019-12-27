package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemList
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    var machineItems: MutableLiveData<List<Item>>
    var shelfItems: MutableLiveData<List<Item>>

    public val allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.i(TAG, "HomeViewModel created!")

        allGroupItems = MutableLiveData()
        machineItems = MutableLiveData()
        shelfItems = MutableLiveData()

        val machineId = "yDWzDc79Uu1IO2lEeVyG"  //TODO: machineId must be passed to the function
        getItemsFromMachine(machineId)
        getItemsInShelf(uid)

    }

    private fun getItemsFromMachine(machineId: String) {

        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getMachineItemsAsync(machineId)
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult ")

                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                machineItems.value = moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items

                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    private fun getItemsInShelf(userId: String) {
        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getShelfItemsAsync(userId)
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult ")

                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                shelfItems.value = moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items

                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    fun updateItemGroupModel(){

        val shelfItemsInMachine: ArrayList<Item> = ArrayList()

        for(i in machineItems.value!!.indices ) {
            for(j in shelfItems.value!!.indices) {
                if(machineItems.value!![i].id == shelfItems.value!![j].id) {
                    shelfItems.value!![j].inMachine = true
                    shelfItems.value!![j].remainingInMachine = machineItems.value!![i].remainingInMachine
                    shelfItemsInMachine.add(shelfItems.value!![j])
                }
            }
        }

        val temp = ArrayList<ItemGroupModel>()

        if(shelfItemsInMachine.isNotEmpty()) {
            val shelfItemsInMachineGroupModel = ItemGroupModel(
                items = shelfItemsInMachine,
                draw_line_breaker = machineItems.value!!.isNotEmpty()
            )
            temp.add(shelfItemsInMachineGroupModel)
        }
        if(machineItems.value!!.isNotEmpty()) {
            val machineItemsGroupModel = ItemGroupModel(
                items = machineItems.value!!,
                draw_line_breaker = shelfItems.value!!.isNotEmpty()
            )
            temp.add(machineItemsGroupModel)
        }
        if(shelfItems.value!!.isNotEmpty()) {
            val shelfItemsGroupModel = ItemGroupModel(
                items = shelfItems.value!!,
                draw_line_breaker = false
            )
            temp.add(shelfItemsGroupModel)
        }
        
        allGroupItems.value = temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
        viewModelJob.cancel()
    }
}