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
import androidx.lifecycle.ViewModelProvider
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScannerState
import com.xborg.vendx.activities.vendingActivity.fragments.deviceScanner.DeviceScanner
import com.xborg.vendx.database.VendingState
import java.util.*
import kotlin.concurrent.schedule

var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var broadcastReceiver: BroadcastReceiver

    var selectedMachineFound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        sharedViewModel.vendingState.observe(this, Observer { state ->
            Log.i(TAG, "VendingState: $state")
            when (state) {
//                VendingState.Init -> TODO()
//                VendingState.DeviceDiscovered -> TODO()
//                VendingState.ConnectionRequest -> TODO()
//                VendingState.Connecting -> TODO()
//                VendingState.Connected -> TODO()
                VendingState.ReceivedOtp -> sharedViewModel.sendEncryptedOtpToServer()
//                VendingState.ReceivedOtpWithBag -> TODO()
//                VendingState.Vending -> TODO()
//                VendingState.VendingDone -> TODO()
//                VendingState.VendingComplete -> TODO()
                VendingState.ReceivedLog -> sharedViewModel.sendEncryptedDeviceLogToServer()
//                VendingState.ReceivedLogAck -> TODO()
//                VendingState.Error -> TODO()
            }
        })

        sharedViewModel.deviceConnectionState.observe(this, Observer { state ->
            Log.i(TAG, "deviceConnectionState: $state")

            when (state) {
                DeviceScannerState.None -> {

                }
                DeviceScannerState.DeviceInfoSet -> {

                }
                DeviceScannerState.ScanMode -> {
                    scanForSelectedMachine()
                }
                DeviceScannerState.DeviceNearby -> {

                }
                DeviceScannerState.DeviceNotNearby -> {

                }
                DeviceScannerState.DeviceIdle -> {

                }
                DeviceScannerState.DeviceBusy -> {

                }
            }
        })

        loadScannerFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun scanForSelectedMachine() {
        Log.i(TAG, "scan for selected machine")
        selectedMachineFound = false
        intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        broadcastReceiver = object : BroadcastReceiver() {
            @SuppressLint("DefaultLocale")
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent!!.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        if (!selectedMachineFound) {
                            val device: BluetoothDevice? =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            if (sharedViewModel.selectedMachine.value!!.Mac.toUpperCase() == device!!.address.toUpperCase()) {
                                Log.i(TAG, "found selected machine : " + device.address)
                                sharedViewModel.deviceConnectionState.value =
                                    DeviceScannerState.DeviceNearby
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
            if (!selectedMachineFound) {
                sharedViewModel.deviceConnectionState.postValue(DeviceScannerState.DeviceNotNearby)
            }
        }
    }

    //      region Fragment Loading
    @SuppressLint("ResourceType")
    private fun loadScannerFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.bluetooth_container,
            DeviceScanner(),
            "DeviceScanner"
        )
        fragmentTransaction.commitNowAllowingStateLoss()
    }
}