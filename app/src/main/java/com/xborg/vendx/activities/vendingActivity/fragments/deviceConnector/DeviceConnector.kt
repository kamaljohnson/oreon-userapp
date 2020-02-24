package com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothAdapter.getDefaultAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.database.Machine
import com.xborg.vendx.preferences.SharedPreference

const val TAG: String = "DeviceConnector"

class DeviceConnector : Fragment() {

    private enum class ScanState {
        NONE, LESCAN, DISCOVERY, DISCOVERY_FINISHED
    }
    private var scanState = ScanState.NONE
    private val LESCAN_PERIOD: Long = 10000 // similar to bluetoothAdapter.startDiscovery
    private val leScanStopHandler = Handler()
    private var leScanCallback: LeScanCallback? = null
    lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var viewModel: DeviceConnectorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    init {
        leScanCallback =
            LeScanCallback { device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray? ->
                if (device != null && activity != null) {
                    activity!!.runOnUiThread { updateScan(device) }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Device Connector Loaded")

        bluetoothAdapter = getDefaultAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_connector, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DeviceConnectorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.selectedMachine.observe(viewLifecycleOwner, Observer { machine ->
            if(machine != null) {
                sharedViewModel.selectedMachine.value = machine
                viewModel.deviceScanningMode.value = true
                sharedViewModel.deviceConnectionState.value = DeviceConnectionState.DeviceInfo
            }
        })

        sharedViewModel.deviceConnectionState.observe(viewLifecycleOwner, Observer {state ->
            viewModel.deviceConnectionState.value = state
            when(state) {
                DeviceConnectionState.DeviceInfo -> {
                    val machine = viewModel.selectedMachine.value
                    if(machine != null) {
                        Log.i(TAG, "device info loaded")
                        sharedViewModel.selectedMachine.value = machine
                        sharedViewModel.deviceConnectionState.value = DeviceConnectionState.ScanMode
                    }
                }
                DeviceConnectionState.ScanMode -> {
                    Log.i(TAG, "device scanning mode")
                }
                DeviceConnectionState.DeviceNearby -> {
                    Log.i(TAG, "selected device is nearby")
                    startLeScan()
                }
                DeviceConnectionState.DeviceNotNearby -> {
                    Log.i(TAG, "selected device is not nearby")
                }
                DeviceConnectionState.DeviceIdle -> {
                    Log.i(TAG, "selected device is idle")
                }
                DeviceConnectionState.DeviceBusy -> {
                    Log.i(TAG, "selected device is busy")
                }
                DeviceConnectionState.DeviceConnected -> {
                    Log.i(TAG, "selected device connected")
                }
            }
        })
        getSelectedMachineMac()
    }

    private fun getSelectedMachineMac() {
        val machine = Machine()
        val preference = SharedPreference(context!!)
        machine.Mac = preference.getSelectedMachineMac()!!
        viewModel.selectedMachine.value = machine
    }

    @SuppressLint("StaticFieldLeak")
    private fun startLeScan() {
        scanState = ScanState.LESCAN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationManager =
                activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var locationEnabled = false
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ignored: Exception) {
            }
            try {
                locationEnabled =
                    locationEnabled or locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ignored: Exception) {
            }
            if (!locationEnabled) scanState = ScanState.DISCOVERY
        }
        if (scanState == ScanState.LESCAN) {
            leScanStopHandler.postDelayed(this::stopLeScan, LESCAN_PERIOD)
            object : AsyncTask<Void?, Void?, Void?>() {
                override fun doInBackground(params: Array<Void?>): Void? {
                    bluetoothAdapter.startLeScan(null, leScanCallback)
                    return null
                }
            }.execute() // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        }
    }

    private fun updateScan(device: BluetoothDevice) {
        if (scanState == ScanState.NONE) return
        if(device.address.toUpperCase() == viewModel.selectedMachine.value!!.Mac.toUpperCase()) {
            Log.i(TAG, "device discovered using ble: " +  device.address)
            sharedViewModel.deviceConnectionState.value = DeviceConnectionState.DeviceIdle
            stopLeScan()
        }
    }

    private fun stopLeScan() {
        if (scanState == ScanState.NONE) return
        when (scanState) {
            ScanState.LESCAN -> {
                leScanStopHandler.removeCallbacks { stopLeScan() }
                bluetoothAdapter.stopLeScan(leScanCallback)
            }
        }
        scanState = ScanState.DISCOVERY_FINISHED
        if(sharedViewModel.deviceConnectionState.value!! < DeviceConnectionState.DeviceIdle) {
            sharedViewModel.deviceConnectionState.value = DeviceConnectionState.DeviceBusy
        }
    }
}
