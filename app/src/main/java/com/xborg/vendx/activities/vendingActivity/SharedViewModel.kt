package com.xborg.vendx.activities.vendingActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.database.Bag
import com.xborg.vendx.database.BagStatus

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    val bag = MutableLiveData<Bag>()
    val bagStatus = MutableLiveData<BagStatus>()

    init {
        bagStatus.value = BagStatus.None
        bag.value = Bag(
            status = BagStatus.None,
            mid = "yDWzDc79Uu1IO2lEeVyG",
            uid = uid
        )      // TODO: get the mid from paymentActivity
    }

}