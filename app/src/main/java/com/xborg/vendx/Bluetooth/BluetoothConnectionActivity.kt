package com.xborg.vendx.Bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus
import com.xborg.vendx.BuildConfig
import com.xborg.vendx.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.VendingActivity
import kotlinx.android.synthetic.main.activity_bluetooth_connection.*

import java.util.Arrays

const val TAG = "BluetoothConnection"
private const val REQUEST_ENABLE_LOC = 3

class BluetoothConnectionActivity : AppCompatActivity(), BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mService: BluetoothService? = null
    private var mScanning: Boolean = false

    private var deviceConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connection)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mService = BluetoothService.getDefaultInstance()

        mService!!.setOnScanCallback(this)
        mService!!.setOnEventCallback(this)

        deviceConnected = false

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Location permission required for connecting to the machine")
                .setPositiveButton(R.string.Ok) { _, _ ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_ENABLE_LOC
                    )
                }
            builder.create()
            builder.show()
        } else {
            mService!!.startScan()
        }

        try_again.setOnClickListener {
            mService!!.startScan()
        }

    }

    override fun onResume() {
        super.onResume()
        mService!!.setOnEventCallback(this)
    }

    override fun onRestart() {
        super.onRestart()
        deviceConnected = false
    }

    override fun onDeviceDiscovered(device: BluetoothDevice, rssi: Int) {
        Log.d(TAG, "onDeviceDiscovered: " + device.name + " - " + device.address + " - " + Arrays.toString(device.uuids))
        if(device.address == "3C:71:BF:79:86:22") {
            mService?.connect(device)
        }
    }

    override fun onStartScan() {
        Log.d(TAG, "onStartScan")
        mScanning = true
    }

    override fun onStopScan() {
        Log.d(TAG, "onStopScan")
        if(!deviceConnected) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Could'nt find near-by vending machines, please stand near the machine and try again")
                .setPositiveButton(R.string.Ok) { _, _ ->
                    try_again.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    connection_status_text.text = "device connection error"
                }
            builder.create()
            builder.show()
        }
        mScanning = false
    }

    override fun onDataRead(buffer: ByteArray, length: Int) {
        Log.d(TAG, "onDataRead")
    }

    @SuppressLint("DefaultLocale")
    override fun onStatusChange(status: BluetoothStatus) {
        Log.d(TAG, "onStatusChange: $status")
        connection_status_text.text = "${status.toString().toLowerCase()} to device"
        if (status == BluetoothStatus.CONNECTED) {
            deviceConnected = true
            val intent = Intent(this, VendingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    override fun onDeviceName(deviceName: String) {
        Log.d(TAG, "onDeviceName: $deviceName")
    }

    override fun onToast(message: String) {
        Log.d(TAG, "onToast")
    }

    override fun onDataWrite(buffer: ByteArray) {
        Log.d(TAG, "onDataWrite")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ENABLE_LOC -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mService!!.startScan()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("The transaction will be cancelled if the device is not able to connect to your phone, please allow access to location to connect to the nearby device")
                        .setPositiveButton(R.string.Ok) { _, _ ->
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                                REQUEST_ENABLE_LOC
                            )
                        }
                        .setNegativeButton(R.string.cancel) { dialog, id ->
                            // User cancelled the dialog
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    builder.create()
                    builder.show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
        }
    }
}