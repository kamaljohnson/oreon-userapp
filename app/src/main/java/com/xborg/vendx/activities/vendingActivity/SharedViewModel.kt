package com.xborg.vendx.activities.vendingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator.DeviceConnectionStatus
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState
import com.xborg.vendx.database.VendingStatus

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    val bag = MutableLiveData<Vend>()
    val vendState = MutableLiveData<VendingState>()
    val deviceConnectionStatus = MutableLiveData<DeviceConnectionStatus>()
    val retryDeviceConnection = MutableLiveData<Boolean>()

    private val currentVendingCount = MutableLiveData<Int>()

    init {
        retryDeviceConnection.value = false
        deviceConnectionStatus.value = DeviceConnectionStatus.None

        vendState.value = VendingState.Init
        bag.value = Vend(
            Status = VendingStatus.Init,
            mid = "yDWzDc79Uu1IO2lEeVyG",
            Uid = uid
        )      // TODO: get the mid from paymentActivity
        currentVendingCount.value = 0
    }

    fun updateVendingCount() {
        currentVendingCount.value = currentVendingCount.value!! + 1
        bag.value!!.Status = VendingStatus.Processing
    }
}