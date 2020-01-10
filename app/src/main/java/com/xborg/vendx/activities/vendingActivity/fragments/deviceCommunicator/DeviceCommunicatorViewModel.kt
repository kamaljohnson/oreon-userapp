package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState

class DeviceCommunicatorViewModel : ViewModel() {


    val bag = MutableLiveData<Vend>()
    val bagStatus = MutableLiveData<VendingState>()

    init {
        bagStatus.value = VendingState.None
    }

    fun addEncryptedOtp(encryptedOtp: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.encryptedOtp = encryptedOtp
        bagStatus.value = VendingState.EncryptedOtpReceived
    }
}