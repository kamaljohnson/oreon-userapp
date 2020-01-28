package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.activities.loginActivity.db
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemGroup
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.reflect.Type

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    var debugText: MutableLiveData<String> = MutableLiveData()

    val uid = FirebaseAuth.getInstance().uid.toString()

    val apiCallError = MutableLiveData<Boolean>()

    val selectedMachine = MutableLiveData<Machine>()
    val selectedMachineLoaded = MutableLiveData<Boolean>()

    var machineItems: MutableLiveData<List<Item>>
    var inventoryItems: MutableLiveData<List<Item>>

    val allGroupItems: MutableLiveData<ArrayList<ItemGroup>>

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.i(TAG, "HomeViewModel created!")
        allGroupItems = MutableLiveData()
        machineItems = MutableLiveData()
        inventoryItems = MutableLiveData()

        machineItems.value = ArrayList()
        inventoryItems.value = ArrayList()

        selectedMachine.value = Machine()
        debugText.value = "init home\n\n"
        handleInventoryUpdates()
    }

    private fun handleInventoryUpdates() {
        debugText.value = "handle shelf updates\n"
        //Checking if user shelf is updated in server
        val docRef = db.collection("Users").document(uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.metadata.hasPendingWrites())
                Log.i(TAG, "no changes in server")
            else
                getItemsInInventory(uid)
        }
    }

    fun changedSelectedMachine() {
        selectedMachineLoaded.value = false
        machineItems.value = ArrayList()
        if (selectedMachine.value != null) {
            updateItemGroupModel()
            if(selectedMachine.value!!.Id != "") {
                Log.i(TAG, "machine Id : " + selectedMachine.value!!.Id)
                getItemsFromMachine(selectedMachine.value!!.Id)
            } else {
                updateItemGroupModel()
            }
        }

        //Checking if current selected machine is updated in server
        if (selectedMachine.value!!.Id == "") return
        val docRef = db.collection("Machines").document(selectedMachine.value!!.Id)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.metadata.hasPendingWrites())
                Log.i(TAG, "no changes in server")
            else
                getItemsFromMachine(selectedMachine.value!!.Id)
        }
    }

    //TODO: combine both items from machine and self to single get req
    private fun getItemsFromMachine(machineId: String) {
        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getMachineItemsAsync(id = machineId)
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")
                debugText.value = "Successful to get response: $listResult\n\n"

                val itemListType: Type = object : TypeToken<ArrayList<Item?>?>() {}.type
                machineItems.value = Gson().fromJson(listResult, itemListType)!!
                selectedMachineLoaded.value = true

                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.e(TAG, "Machine Items: Failed to get response: ${t.message}")
                debugText.value = "Inventory Items: Failed to get response: ${t.message}\n\n"

                apiCallError.value = true
            }
        }
    }

    private fun getItemsInInventory(userId: String) {
        debugText.value = "get items from shelf\n\n"
        coroutineScope.launch {
            val getMachineItemsDeferred = VendxApi.retrofitServices.getInventoryItemsAsync(userId)
            try {
                val listResult = getMachineItemsDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult ")
                debugText.value = "Successful to get response: $listResult\n\n"

                val itemListType: Type = object : TypeToken<ArrayList<Item?>?>() {}.type
                inventoryItems.value = Gson().fromJson(listResult, itemListType)!!
                Log.i(TAG, "Inventory: " + inventoryItems.value.toString())
                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.i(TAG, "Inventory Items: Failed to get response: ${t.message}")
                debugText.value = "Inventory Items: Failed to get response: ${t.message}\n\n"
                apiCallError.value = true
            }
        }
    }

    private fun updateItemGroupModel() {
        val shelfItemsInMachine: ArrayList<Item> = ArrayList()

        for (i in machineItems.value!!.indices) {
            for (j in inventoryItems.value!!.indices) {
                if (machineItems.value!![i].Id == inventoryItems.value!![j].Id) {
                    inventoryItems.value!![j].InMachine = true
                    inventoryItems.value!![j].RemainingInMachine =
                        machineItems.value!![i].RemainingInMachine
                    shelfItemsInMachine.add(inventoryItems.value!![j])
                }
            }
        }


        val temp = ArrayList<ItemGroup>()

        if (shelfItemsInMachine.isNotEmpty()) {
            val shelfItemsInMachineGroupModel =
                ItemGroup(
                    Title = "From Inventory",
                    Items = shelfItemsInMachine,
                    DrawLineBreaker = machineItems.value!!.isNotEmpty()
                )
            temp.add(shelfItemsInMachineGroupModel)
        }

        Log.i(TAG, "code : " + selectedMachine.value!!.Code + " loaded : " + selectedMachineLoaded.value)

        if(selectedMachineLoaded.value == true) {
            if (machineItems.value!!.isNotEmpty()) {
                Log.i(TAG, "machine loaded")
                val machineItemsGroupModel = ItemGroup(
                    Title = "In Machine",
                    Items = machineItems.value!!,
                    DrawLineBreaker = inventoryItems.value!!.isNotEmpty()
                )
                temp.add(machineItemsGroupModel)
            }
        } else if(selectedMachine.value!!.Code == "Dummy") {
            Log.i(TAG, "no machine near")
            val machineItemsGroupModel = ItemGroup(
                Title = "Machine",
                DrawLineBreaker = inventoryItems.value!!.isNotEmpty(),
                ShowNoMachinesNearbyMessage = true
            )
            temp.add(machineItemsGroupModel)
        } else {
            Log.i(TAG, "loading..")
            val machineItemsGroupModel = ItemGroup(
                Title = "Machine",
                DrawLineBreaker = inventoryItems.value!!.isNotEmpty()
            )
            temp.add(machineItemsGroupModel)
        }

        val shelfItemsNotInMachine: ArrayList<Item> = ArrayList()
        inventoryItems.value!!.forEach { s_item ->
            var flag = true
            machineItems.value!!.forEach { m_item ->
                if (s_item.Id == m_item.Id) {
                    flag = false
                }
            }
            if (flag) {
                shelfItemsNotInMachine.add(s_item)
            }
        }

        if (shelfItemsNotInMachine.isNotEmpty()) {
            val shelfItemsNotInMachineGroupModel = ItemGroup(
                Title = if (machineItems.value!!.isEmpty()) {
                    "Inventory"
                } else {
                    "Remaining Inventory"
                },
                Items = shelfItemsNotInMachine,
                DrawLineBreaker = false
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