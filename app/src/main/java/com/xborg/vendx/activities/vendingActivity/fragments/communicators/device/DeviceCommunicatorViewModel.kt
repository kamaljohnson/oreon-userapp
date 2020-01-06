package com.xborg.vendx.activities.vendingActivity.fragments.communicators.device

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Bag
import com.xborg.vendx.database.BagStatus

class DeviceCommunicatorViewModel : ViewModel() {

    val bag = MutableLiveData<Bag>()

    fun initBag() {
        bag.value = Bag(status = BagStatus.Init)
    }

    fun addOtp(encryptedOtp: String) {
        val tempBag = bag.value!!
        tempBag.encryptedOtp = encryptedOtp
        tempBag.status = BagStatus.OtpReceived
        bag.value = tempBag
    }
}