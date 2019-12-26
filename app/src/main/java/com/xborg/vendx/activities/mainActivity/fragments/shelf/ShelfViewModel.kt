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

    var shelfItems: List<Item> = ArrayList()
    var machineItems: List<Item> = ArrayList()

    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    val allGroupItems: LiveData<ArrayList<ItemGroupModel>>
        get() = _allGroupItems

    init {
        Log.i(TAG, "ShelfViewModel created!")

        _allGroupItems = MutableLiveData()
    }

    fun updateItemGroupModel() {

        for(i in machineItems.indices ) {
            for(j in shelfItems.indices) {
                if(machineItems[i].id == shelfItems[j].id) {
                    shelfItems[j].inMachine = true
                }
            }
        }

        val shelfItemsGroupModel = ItemGroupModel(
            items = shelfItems,
            draw_line_breaker = false
        )

        val temp = ArrayList<ItemGroupModel>()
        temp.add(shelfItemsGroupModel)

        _allGroupItems.value = temp
    }
}