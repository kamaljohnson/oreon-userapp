package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState
import com.xborg.vendx.database.VendingStatus

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    val bag = MutableLiveData<Vend>()
    val bagState = MutableLiveData<VendingState>()

    val currentVendingCount = MutableLiveData<Int>()

    init {
        bagState.value = VendingState.Init
        bag.value = Vend(
            status = VendingStatus.Init,
            mid = "yDWzDc79Uu1IO2lEeVyG",
            uid = uid
        )      // TODO: get the mid from paymentActivity
        currentVendingCount.value = 0
    }

    fun updateVendingCount() {
        currentVendingCount.value = currentVendingCount.value!! + 1
        bag.value!!.status = VendingStatus.Processing
    }
}