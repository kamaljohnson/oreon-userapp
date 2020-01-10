package com.xborg.vendx.activities.vendingActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator.DeviceCommunicatorFragment
import com.xborg.vendx.activities.vendingActivity.fragments.status.VendingStatusFragment

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)
        loadFragments()

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        sharedViewModel.bagStatus.observe(this, Observer { updatedBag ->
            Log.i(TAG, "VendingState : $updatedBag")
        })
    }

    private fun loadFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.device_communicator_fragment_container,
            DeviceCommunicatorFragment(),
            "DeviceCommunicatorFragment"
        )
        fragmentTransaction.add(
            R.id.vending_status_fragment_container,
            VendingStatusFragment(),
            "VendingStatusFragment"
        )
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //TODO: do according to vending status
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}