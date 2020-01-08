package com.xborg.vendx.activities.mainActivity.fragments.shelf

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemGroup

private const val TAG = "ShelfViewModel"

class ShelfViewModel: ViewModel() {

    var shelfItems: List<Item> = ArrayList()
    var machineItems: List<Item> = ArrayList()

    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroup>>
    val allGroupItems: LiveData<ArrayList<ItemGroup>>
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

        val shelfItemsGroupModel = ItemGroup(
            title = "From Shelf",
            items = shelfItems,
            draw_line_breaker = false
        )

        val temp = ArrayList<ItemGroup>()
        temp.add(shelfItemsGroupModel)

        _allGroupItems.value = temp
    }
}