package com.xborg.vendx.activities.vendingActivity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector.DeviceConnector
import java.util.*
import kotlin.concurrent.schedule

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        sharedViewModel.deviceScanningMode.observe(this, Observer { enabled ->
            if(enabled) {
                scanForSelectedMachine()
            }
        })
        loadFragments()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun scanForSelectedMachine() {
        intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        var selectedMachineFound = false
        broadcastReceiver = object: BroadcastReceiver() {
            @SuppressLint("DefaultLocale")
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent!!.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        if(!selectedMachineFound) {
                            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            Log.i(TAG, "found : " + device!!.address + ", required " + sharedViewModel.selectedMachine.value)
                            if(sharedViewModel.selectedMachine.value!!.Mac.toUpperCase() == device.address.toUpperCase()) {
                                sharedViewModel.selectedMachineNearby.value = true
                                selectedMachineFound = true
                            }
                        }
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isDiscovering) {
            // Bluetooth is already in mode discovery mode, we cancel to restart it again
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
        Timer("discovery timer", false).schedule(10000) {
            Log.i(TAG, "discovery finished")
            bluetoothAdapter.cancelDiscovery()
            if(!selectedMachineFound) {
                sharedViewModel.selectedMachineNearby.postValue(false)
            }
        }
    }

    //      region Fragment Loading
    @SuppressLint("ResourceType")
    private fun loadFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.device_connector_fragment_container,
            DeviceConnector(),
            "DeviceConnector"
        )
        fragmentTransaction.commitNowAllowingStateLoss()
    }
}