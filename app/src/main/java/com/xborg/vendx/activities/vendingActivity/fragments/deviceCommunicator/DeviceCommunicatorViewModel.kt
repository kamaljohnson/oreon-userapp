package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState

class DeviceCommunicatorViewModel : ViewModel() {


    val bag = MutableLiveData<Vend>()
    val vendState = MutableLiveData<VendingState>()

    init {
        vendState.value = VendingState.Init
    }

    fun addEncryptedOtp(encryptedOtp: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.encryptedOtp = encryptedOtp
        vendState.value = VendingState.EncryptedOtpReceivedFromDevice
    }

    fun addEncryptedLog(encryptedLog: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.encryptedLog = encryptedLog
        vendState.value = VendingState.EncryptedDeviceLogReceivedFromDevice
    }
}