package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    val bag = MutableLiveData<Vend>()
    val bagStatus = MutableLiveData<VendingState>()

    init {
        bagStatus.value = VendingState.None
        bag.value = Vend(
            state = VendingState.None,
            mid = "yDWzDc79Uu1IO2lEeVyG",
            uid = uid
        )      // TODO: get the mid from paymentActivity
    }
}