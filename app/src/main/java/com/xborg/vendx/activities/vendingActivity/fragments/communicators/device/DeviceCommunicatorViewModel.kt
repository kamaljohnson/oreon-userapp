package com.xborg.vendx.activities.vendingActivity.fragments.communicators.device

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Bag
import com.xborg.vendx.database.BagStatus

class DeviceCommunicatorViewModel : ViewModel() {


    val bag = MutableLiveData<Bag>()
    val bagStatus = MutableLiveData<BagStatus>()

    init {
        bagStatus.value = BagStatus.None
    }

    fun addEncryptedOtp(encryptedOtp: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.encryptedOtp = encryptedOtp
        bagStatus.value = BagStatus.EncryptedOtpReceived
    }
}