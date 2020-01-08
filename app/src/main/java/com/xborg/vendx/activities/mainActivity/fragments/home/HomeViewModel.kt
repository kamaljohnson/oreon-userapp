package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemList
import com.xborg.vendx.database.ItemGroup
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    var machineItems: MutableLiveData<List<Item>>
    var shelfItems: MutableLiveData<List<Item>>

    val allGroupItems: MutableLiveData<ArrayList<ItemGroup>>

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

    //TODO: combine both items from machine and self to single get req
    private fun getItemsFromMachine(machineId: String) {

        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getMachineItemsAsync(machineId)
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                machineItems.value =
                    moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items

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

    private fun updateItemGroupModel() {

        val shelfItemsInMachine: ArrayList<Item> = ArrayList()

        for (i in machineItems.value!!.indices) {
            for (j in shelfItems.value!!.indices) {
                if (machineItems.value!![i].id == shelfItems.value!![j].id) {
                    shelfItems.value!![j].inMachine = true
                    shelfItems.value!![j].remainingInMachine =
                        machineItems.value!![i].remainingInMachine
                    shelfItemsInMachine.add(shelfItems.value!![j])
                }
            }
        }

        val temp = ArrayList<ItemGroup>()

        if (shelfItemsInMachine.isNotEmpty()) {
            val shelfItemsInMachineGroupModel =
                ItemGroup(
                    title = "From Shelf",
                    items = shelfItemsInMachine,
                    draw_line_breaker = machineItems.value!!.isNotEmpty()
                )
            temp.add(shelfItemsInMachineGroupModel)
        }
        if (machineItems.value!!.isNotEmpty()) {
            val machineItemsGroupModel = ItemGroup(
                title = "In Machine",
                items = machineItems.value!!,
                draw_line_breaker = shelfItems.value!!.isNotEmpty()
            )
            temp.add(machineItemsGroupModel)
        }

        val shelfItemsNotInMachine: ArrayList<Item> = ArrayList()
        shelfItems.value!!.forEach{ s_item ->
            var flag = true
            machineItems.value!!.forEach { m_item ->
                if(s_item.id == m_item.id) {
                    flag = false
                }
            }
            if(flag) {
                shelfItemsNotInMachine.add(s_item)
            }
        }

        if (shelfItemsNotInMachine.isNotEmpty()) {
            val shelfItemsNotInMachineGroupModel = ItemGroup(
                title = "Remaining Shelf",
                items = shelfItemsNotInMachine,
                draw_line_breaker = false
            )
            temp.add(shelfItemsNotInMachineGroupModel)
        }

        allGroupItems.value = temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "destroyed!")
        viewModelJob.cancel()
    }
}