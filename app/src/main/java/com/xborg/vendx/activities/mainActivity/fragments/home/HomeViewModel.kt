package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xborg.vendx.database.*
import com.xborg.vendx.database.machine.Machine
import com.xborg.vendx.database.machine.MachineDatabase
import com.xborg.vendx.database.user.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    var userInventory =  MutableLiveData<List<InventoryItem>>()

    var homeInventoryGroups = MutableLiveData<List<HomeInventoryGroups>>()

    val userDao = UserDatabase.getInstance(application).userDao()

    val machineDao = MachineDatabase.getInstance(application).machineDao()

    companion object {
        private var viewModelJob = Job()
        private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

        var selectedMachine = MutableLiveData<Machine>()

        lateinit var context: Application

        fun cartProcessor(cartDao: CartItemDao) {
            Toast.makeText(context, "No machines selected", Toast.LENGTH_SHORT).show()
            if(selectedMachine.value!!.Inventory.isEmpty()) {
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

        var selectedMachineInventoryGroup: HomeInventoryGroups
        var userInventoryGroup: HomeInventoryGroups

        if(selectedMachine.value!!.Inventory.isEmpty()) {
            try {

                selectedMachineInventoryGroup = HomeInventoryGroups(
                    Title = "Machine",
                    Message = "Could'nt find \nMachines near you",
                    PaidInventory = false
                )

                userInventoryGroup = HomeInventoryGroups(
                    Title = "Inventory",
                    Inventory = userInventory.value!!,
                    Message = "",
                    PaidInventory = true
                )

                val newHomeInventoryGroups: ArrayList<HomeInventoryGroups> = ArrayList()

                newHomeInventoryGroups.add(selectedMachineInventoryGroup)
                newHomeInventoryGroups.add(userInventoryGroup)

                homeInventoryGroups.value = newHomeInventoryGroups

            } catch (e: Error) {

            }

        } else {

            try {

                selectedMachineInventoryGroup = HomeInventoryGroups(
                    Title = "Machine",
                    Inventory = selectedMachine.value!!.Inventory,
                    Message = "",
                    PaidInventory = false
                )

                userInventoryGroup = HomeInventoryGroups(
                    Title = "Inventory",
                    Inventory = userInventory.value!!,
                    Message = "",
                    PaidInventory = true
                )

                val newHomeInventoryGroups: ArrayList<HomeInventoryGroups> = ArrayList()

                newHomeInventoryGroups.add(selectedMachineInventoryGroup)
                newHomeInventoryGroups.add(userInventoryGroup)

                homeInventoryGroups.value = newHomeInventoryGroups

            } catch(e: Error) {

            }

        }
    }
}