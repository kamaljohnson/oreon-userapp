package com.xborg.vendx.activities.vendingActivity

import androidx.appcompat.app.AppCompatActivity


import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus

private var TAG = "VendingActivity"

@Suppress("CAST_NEVER_SUCCEEDS")
class VendingActivity : AppCompatActivity(), BluetoothService.OnBluetoothEventCallback {
    override fun onDataRead(buffer: ByteArray?, length: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChange(status: BluetoothStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataWrite(buffer: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onToast(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeviceName(deviceName: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}