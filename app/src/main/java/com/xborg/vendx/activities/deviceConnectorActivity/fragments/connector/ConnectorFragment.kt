package com.xborg.vendx.activities.deviceConnectorActivity.fragments.connector

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus

import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.VendingActivity
import java.util.*

private const val TAG = "ConnectorFragment"

class ConnectorFragment : Fragment(), BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback {
    // TODO: Rename and change types of parameters

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mService: BluetoothService? = null
    private var mScanning: Boolean = false

    private var deviceDiscovered: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mService = BluetoothService.getDefaultInstance()

        mService!!.setOnScanCallback(this)
        mService!!.setOnEventCallback(this)

        deviceDiscovered = false
        mService!!.startScan()

    }
    override fun onStopScan() {
        Log.d(TAG, "onStopScan")
        if(!deviceDiscovered) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Could'nt find near-by vending machines, please stand near the machine and try again")
                .setPositiveButton(R.string.Ok) { _, _ ->
//                    try_again.visibility = View.VISIBLE
//                    progressBar.visibility = View.INVISIBLE
//                    connection_status_text.text = "device connection error"
                }
            builder.create()
            builder.show()
            mScanning = false
        }
    }

    override fun onStartScan() {
        Log.d(TAG, "onStartScan")
        mScanning = true    }

    override fun onDeviceDiscovered(device: BluetoothDevice, rssi: Int) {
        Log.d(TAG, "onDeviceDiscovered: " + device.name + " - " + device.address + " - " + Arrays.toString(device.uuids))
        if(device.address == "3C:71:BF:79:86:22") {
            mService?.connect(device)
        }
    }

    override fun onDataRead(buffer: ByteArray?, length: Int) {
        Log.d(TAG, "onDataRead")
    }

    override fun onStatusChange(status: BluetoothStatus?) {
        Log.d(TAG, "onStatusChange: $status")
//        connection_status_text.text = "${status.toString().toLowerCase()} to device"
        if(status == BluetoothStatus.CONNECTING) {
            deviceDiscovered = true
        }
        if (status == BluetoothStatus.CONNECTED) {
            val intent = Intent(activity, VendingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onDataWrite(buffer: ByteArray?) {
        Log.d(TAG, "onDataWrite")
    }

    override fun onToast(message: String?) {
        Log.d(TAG, "onToast")
    }

    override fun onDeviceName(deviceName: String?) {
        Log.d(TAG, "onDeviceName: $deviceName")
    }
}
