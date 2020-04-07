package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
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
            if(selectedMachine.value!!.Inventory.isEmpty()) {
                return
            }

            selectedMachine.value!!.Inventory.forEach { item ->

                val limit = item.Quantity

                ioScope.launch {
                    cartDao.processCart(item.ItemDetailId, limit)
                }
            }
        }
    }

    init {
        context = application
        selectedMachine.value = Machine()
    }

    fun updateHomeInventoryGroups() {

        if(userInventory.value == null)
            return

        val commonInventory = ArrayList<InventoryItem>()
        var remainingUserInventory = ArrayList<InventoryItem>()

        val commonInventoryGroup = HomeInventoryGroups(
            Title = "From Inventory",
            PaidInventory = true
        )
        val machineInventoryGroup = HomeInventoryGroups(
            Title = "In Machine",
            PaidInventory = false
        )
        val remainingUserInventoryGroup = HomeInventoryGroups(
            Title = "Remaining Inventory",
            PaidInventory = true
        )

        val newHomeInventoryGroups: ArrayList<HomeInventoryGroups> = ArrayList()

        if(selectedMachine.value == null) {

            machineInventoryGroup.Message = "No machines \nselected"
            remainingUserInventory = userInventory.value as ArrayList<InventoryItem>
            remainingUserInventoryGroup.Title = "Inventory"

        }
        else if(selectedMachine.value!!.Inventory.isNotEmpty()) {

            machineInventoryGroup.Inventory = selectedMachine.value!!.Inventory

            if(userInventory.value!!.isNotEmpty()) {

                userInventory.value!!.forEach { userItem ->

                    var itemCommon = false

                    selectedMachine.value!!.Inventory.forEach { machineItem ->

                        if(machineItem.ItemDetailId == userItem.ItemDetailId) {
                            itemCommon = true
                            commonInventory.add(userItem)
                        }

                    }

                    if(!itemCommon) {

                        remainingUserInventory.add(userItem)

                    }

                }

                commonInventoryGroup.Inventory = commonInventory
                remainingUserInventoryGroup.Inventory = remainingUserInventory


            } else {

                remainingUserInventoryGroup.Message = "Its empty here"
            }

        } else {

            machineInventoryGroup.Message = "Machine Empty"

            if(userInventory.value!!.isNotEmpty()) {

                remainingUserInventoryGroup.Inventory = userInventory.value!!

            } else {

                remainingUserInventoryGroup.Message = "Its empty here"

            }

        }

        if(commonInventory.isNotEmpty()) {

            newHomeInventoryGroups.add(commonInventoryGroup)

        }

        newHomeInventoryGroups.add(machineInventoryGroup)

        if(remainingUserInventory.isNotEmpty() || commonInventory.isEmpty()) {

            newHomeInventoryGroups.add(remainingUserInventoryGroup)

        }

        homeInventoryGroups.value = newHomeInventoryGroups
    }
}