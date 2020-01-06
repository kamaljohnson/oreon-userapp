package com.xborg.vendx.activities.vendingActivity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.fragments.communicators.device.DeviceCommunicatorFragment
import com.xborg.vendx.activities.vendingActivity.fragments.communicators.server.ServerCommunicatorFragment

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)
        loadFragments()

    }

    private fun loadFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.device_communicator_fragment_container, DeviceCommunicatorFragment(), "DeviceCommunicator")
        fragmentTransaction.add(R.id.server_communicator_fragment_container, ServerCommunicatorFragment(), "ServerCommunicator")
        fragmentTransaction.commit()
    }
}