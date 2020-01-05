package com.xborg.vendx.activities.vendingActivity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity


import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter
import com.xborg.vendx.R

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mService: BluetoothService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mService = BluetoothService.getDefaultInstance()

        Log.i(TAG, "device: " + intent.getParcelableExtra("device"))

        mService!!.connect(intent.getParcelableExtra("device") as BluetoothDevice)
    }
}