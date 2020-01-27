package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Vend
import com.xborg.vendx.database.VendingState

class DeviceCommunicatorViewModel : ViewModel() {


    val bag = MutableLiveData<Vend>()
    val vendState = MutableLiveData<VendingState>()
    val deviceConnectionStatus = MutableLiveData<DeviceConnectionStatus>()

    init {
        vendState.value = VendingState.Init
    }

    fun addEncryptedOtpToBag(encryptedOtp: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.EncryptedOtp = encryptedOtp
        vendState.value = VendingState.EncryptedOtpReceivedFromDevice
    }

    fun addEncryptedLogToBag(encryptedLog: String) {
        Log.i(TAG, bag.value.toString())
        bag.value!!.EncryptedLog = encryptedLog
        vendState.value = VendingState.EncryptedDeviceLogReceivedFromDevice
    }
}