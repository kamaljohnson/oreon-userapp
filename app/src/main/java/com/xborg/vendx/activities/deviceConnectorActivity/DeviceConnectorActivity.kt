package com.xborg.vendx.activities.deviceConnectorActivity

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus
import com.xborg.vendx.R
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.connector.ConnectorFragment
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.prerequisites.PrerequisitesFragment
import com.xborg.vendx.activities.deviceConnectorActivity.fragments.selector.SelectorFragment
import com.xborg.vendx.activities.vendingActivity.VendingActivity
import kotlinx.android.synthetic.main.activity_device_connector.*
import java.util.*

private const val TAG = "DeviceConnection"

class DeviceConnectorActivity : FragmentActivity(){

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_connector)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        sharedViewModel.currentStep.observe(this, Observer {
            Log.i(TAG, "jumping to : " + sharedViewModel.currentStep.value!!)
            jumpToNextStep()
        })
    }

    private fun jumpToNextStep() {
        val fragment: Fragment = when(sharedViewModel.currentStep.value!!) {
            ConnectionSteps.SelectConnectionType -> {
                Log.i(TAG, "calling fragment : " + "Selector")
                SelectorFragment()
            }
            ConnectionSteps.Prerequisites -> {
                Log.i(TAG, "calling fragment : " + "Prerequisites")
                PrerequisitesFragment()
            }
            ConnectionSteps.Connection -> {
                ConnectorFragment()
            }
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(fragment_container.id, fragment)
        fragmentTransaction.commitNowAllowingStateLoss()
    }
}
