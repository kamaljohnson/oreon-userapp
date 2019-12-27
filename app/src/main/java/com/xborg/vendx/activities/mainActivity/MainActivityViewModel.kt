package com.xborg.vendx.activities.mainActivity

import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Item

class MainActivityViewModel: ViewModel() {

    var shelfItems: List<Item> = ArrayList()
    var machineItems: List<Item> = ArrayList()

    fun updateCart() {

    }

}