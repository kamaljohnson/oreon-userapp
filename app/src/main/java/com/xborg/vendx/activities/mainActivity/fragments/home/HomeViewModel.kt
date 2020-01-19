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
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    val selectedMachine = MutableLiveData<Machine>()
    private val selectedMachineLoaded = MutableLiveData<Boolean>()

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

        machineItems.value = ArrayList()
        shelfItems.value = ArrayList()

        selectedMachine.value = Machine()
        getItemsInShelf(uid)
    }

    fun changedSelectedMachine() {
        selectedMachineLoaded.value = false
        machineItems.value = ArrayList()
        if (selectedMachine.value != null) {
            updateItemGroupModel()
            if(selectedMachine.value!!.id != "") {
                Log.i(TAG, "machine Id : " + selectedMachine.value!!.id)
                getItemsFromMachine(selectedMachine.value!!.id)
            } else {
                updateItemGroupModel()
            }
        }
    }

    //TODO: combine both items from machine and self to single get req
    private fun getItemsFromMachine(machineId: String) {
        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getMachineItemsAsync(id = machineId, authToken = "test token")
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                machineItems.value =
                    moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items
                selectedMachineLoaded.value = true

                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.e(TAG, "Machine Items: Failed to get response: ${t.message}")
                apiCallError.value = true
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
                Log.e(TAG, "Shelf Items: Failed to get response: ${t.message}")
                apiCallError.value = true
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
                    drawLineBreaker = machineItems.value!!.isNotEmpty()
                )
            temp.add(shelfItemsInMachineGroupModel)
        }

        Log.i(TAG, "code : " + selectedMachine.value!!.code + " loaded : " + selectedMachineLoaded.value)

        if(selectedMachineLoaded.value == true) {
            if (machineItems.value!!.isNotEmpty()) {
                Log.i(TAG, "machine loaded")
                val machineItemsGroupModel = ItemGroup(
                    title = "In Machine",
                    items = machineItems.value!!,
                    drawLineBreaker = shelfItems.value!!.isNotEmpty()
                )
                temp.add(machineItemsGroupModel)
            }
        } else if(selectedMachine.value!!.code == "Dummy") {
            Log.i(TAG, "no machine near")
            val machineItemsGroupModel = ItemGroup(
                title = "Machine",
                drawLineBreaker = shelfItems.value!!.isNotEmpty(),
                showNoMachinesNearbyMessage = true
            )
            temp.add(machineItemsGroupModel)
        } else {
            Log.i(TAG, "loading..")
            val machineItemsGroupModel = ItemGroup(
                title = "Machine",
                drawLineBreaker = shelfItems.value!!.isNotEmpty()
            )
            temp.add(machineItemsGroupModel)
        }

        val shelfItemsNotInMachine: ArrayList<Item> = ArrayList()
        shelfItems.value!!.forEach { s_item ->
            var flag = true
            machineItems.value!!.forEach { m_item ->
                if (s_item.id == m_item.id) {
                    flag = false
                }
            }
            if (flag) {
                shelfItemsNotInMachine.add(s_item)
            }
        }

        if (shelfItemsNotInMachine.isNotEmpty()) {
            val shelfItemsNotInMachineGroupModel = ItemGroup(
                title = if (machineItems.value!!.isEmpty()) {
                    "Shelf"
                } else {
                    "Remaining Shelf"
                },
                items = shelfItemsNotInMachine,
                drawLineBreaker = false
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