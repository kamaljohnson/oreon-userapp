package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Bag

class SharedViewModel : ViewModel() {

    val bag = MutableLiveData<Bag>()

}