package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.activities.loginActivity.db
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemGroup
import com.xborg.vendx.database.Machine
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

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

    fun handleInventoryUpdates() {
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
        val machineItemsCall = VendxApi.retrofitServices.getMachineItemsAsync(id = machineId)
        machineItemsCall.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
                    machineItems.value = response.body()
                    selectedMachineLoaded.value = true
                    updateItemGroupModel()
                } else {
                    Log.e("Debug", "Failed to get response")
                    apiCallError.value = true
                }
            }

            override fun onFailure(call: Call<List<Item>>, error: Throwable) {
                apiCallError.value = true
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(machineItemsCall.isCanceled) {
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

    private fun getItemsInInventory(userId: String) {
        debugText.value = "get items from shelf\n\n"
        val inventoryItemsCall = VendxApi.retrofitServices.getInventoryItemsAsync(userId)
        inventoryItemsCall.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200 : items: " + response.body())
                    inventoryItems.value = response.body()
                    updateItemGroupModel()
                } else {
                    Log.e("Debug", "Failed to get response")
                    apiCallError.value = true
                }
            }

            override fun onFailure(call: Call<List<Item>>, error: Throwable) {
                apiCallError.value = true
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(inventoryItemsCall.isCanceled) {
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

    private fun updateItemGroupModel() {
        val shelfItemsInMachine: ArrayList<Item> = ArrayList()

        for (i in machineItems.value!!.indices) {
            for (j in inventoryItems.value!!.indices) {
                if (machineItems.value!![i].Id == inventoryItems.value!![j].Id) {
                    inventoryItems.value!![j].FromMachine = true
                    inventoryItems.value!![j].MachineStock =
                        machineItems.value!![i].MachineStock
                    shelfItemsInMachine.add(inventoryItems.value!![j])
                }
            }
        }


        val temp = ArrayList<ItemGroup>()

        if (shelfItemsInMachine.isNotEmpty()) {
            val shelfItemsInMachineGroupModel =
                ItemGroup(
                    Title = "From Inventory",
                    items = shelfItemsInMachine,
                    DrawLineBreaker = machineItems.value!!.isNotEmpty()
                )
            temp.add(shelfItemsInMachineGroupModel)
        }

        Log.i(TAG, "code : " + selectedMachine.value!!.Name + " loaded : " + selectedMachineLoaded.value)

        if(selectedMachineLoaded.value == true) {
            if (machineItems.value!!.isNotEmpty()) {
                Log.i(TAG, "machine loaded")
                val machineItemsGroupModel = ItemGroup(
                    Title = "In Machine",
                    items = machineItems.value!!,
                    DrawLineBreaker = inventoryItems.value!!.isNotEmpty()
                )
                temp.add(machineItemsGroupModel)
            }
        } else if(selectedMachine.value!!.Name == "Dummy") {
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
                items = shelfItemsNotInMachine,
                DrawLineBreaker = false
            )
            temp.add(shelfItemsNotInMachineGroupModel)
        }
        allGroupItems.value = temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "destroyed!")
    }
}