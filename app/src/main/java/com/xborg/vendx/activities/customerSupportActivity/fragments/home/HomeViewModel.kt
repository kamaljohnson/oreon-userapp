package com.xborg.vendx.activities.customerSupportActivity.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.database.RoomSlip


class HomeViewModel : ViewModel() {

    val rooms = MutableLiveData<List<RoomSlip>>()

    init {
        updateRoom()
    }

    private fun updateRoom() {



    }

}
