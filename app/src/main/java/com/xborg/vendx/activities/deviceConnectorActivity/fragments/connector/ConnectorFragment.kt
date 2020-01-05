package com.xborg.vendx.activities.deviceConnectorActivity.fragments.connector

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.activities.vendingActivity.VendingActivity
import java.util.*

private const val TAG = "ConnectorFragment"

class ConnectorFragment : Fragment(), BluetoothService.OnBluetoothScanCallback {
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

        deviceDiscovered = false
        mService!!.startScan()

    }
    override fun onStopScan() {
        Log.d(TAG, "onStopScan")
        if(!deviceDiscovered) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Could'nt find near-by vending machines, please stand near the machine and try again")
                .setPositiveButton(R.string.Ok) { _, _ ->
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
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
            deviceDiscovered = true
            val intent = Intent(activity, VendingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("device", device)
            startActivity(intent)
        }
    }
}
