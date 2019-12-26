package com.xborg.vendx.activities.mainActivity.fragments.shelf

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

private const val TAG = "ShelfViewModel"

class ShelfViewModel: ViewModel() {

    private var uid = FirebaseAuth.getInstance().uid.toString()

    private var shelfItems: List<Item>
    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    val allGroupItems: LiveData<ArrayList<ItemGroupModel>>
        get() = _allGroupItems


    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Log.i(TAG, "ShelfViewModel created!")

        _allGroupItems = MutableLiveData()
        shelfItems = ArrayList()

        getItemsInShelf(uid)
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

                updateItemGroupModel()
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

    private fun updateItemGroupModel(){

//        val shelfItemsInMachine: ArrayList<Item> = ArrayList()
//
//        machineItems.forEach{machineItem->
//            shelfItems.forEach { shelfItem->
//                if(machineItem.id == shelfItem.id) {
//                    shelfItemsInMachine.add(shelfItem)
//                }
//            }
//        }

        val shelfItemsGroupModel = ItemGroupModel(
            items = shelfItems,
            draw_line_breaker = false
        )

        val temp = ArrayList<ItemGroupModel>()
        temp.add(shelfItemsGroupModel)

        _allGroupItems.value = temp
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
        viewModelJob.cancel()
    }
}