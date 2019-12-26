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

    private var uid = FirebaseAuth.getInstance().uid.toString()

    lateinit var machineItems: List<Item>
    lateinit var shelfItems: List<Item>

    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    val allGroupItems: LiveData<ArrayList<ItemGroupModel>>
        get() = _allGroupItems

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.i(TAG, "HomeViewModel created!")

        _allGroupItems = MutableLiveData()

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

                machineItems = moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items

                val itemGroupModel = ItemGroupModel(
                    items = machineItems,
                    draw_line_breaker = false
                )

                val temp = ArrayList<ItemGroupModel>()
                temp.add(itemGroupModel)
                _allGroupItems.value = temp
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

                shelfItems = moshi.adapter(ItemList::class.java).fromJson(listResult)!!.items

//                val itemGroupModel = ItemGroupModel(
//                    items = shelfItems,
//                    draw_line_breaker = false
//                )
//
//                val temp = ArrayList<ItemGroupModel>()
//                temp.add(itemGroupModel)
//                _allGroupItems.value = temp
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
        viewModelJob.cancel()
    }
}