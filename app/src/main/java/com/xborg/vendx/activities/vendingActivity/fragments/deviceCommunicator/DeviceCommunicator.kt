package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.activities.vendingActivity.helper.SerialListener
import com.xborg.vendx.activities.vendingActivity.helper.SerialService
import com.xborg.vendx.activities.vendingActivity.helper.SerialSocket

class DeviceCommunicator : Fragment(), ServiceConnection, SerialListener {

    private enum class Connected {
        False, Pending, True
    }

    private lateinit var viewModel: DeviceCommunicatorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var socket: SerialSocket
    private lateinit var service: SerialService
    private var initialStart: Boolean = true
    private var connected: Connected = Connected.False

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_communicator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity!!.bindService(
            Intent(activity, SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )

        viewModel = ViewModelProviders.of(activity!!).get(DeviceCommunicatorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.selectedMachine.observe(viewLifecycleOwner, Observer { machine ->
            viewModel.selectedMachine.value = machine
        })
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        activity!!.stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service.attach(this) else activity!!.startService(
            Intent(
                activity,
                SerialService::class.java
            )
        ) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    override fun onStop() {
        if (service != null && !activity!!.isChangingConfigurations) service.detach()
        super.onStop()
    }

    override fun onDetach() {
        try {
            activity!!.unbindService(this)
        } catch (ignored: java.lang.Exception) {
        }
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        if (initialStart && service != null) {
            initialStart = false
            activity!!.runOnUiThread { connect() }
        }
    }

    /*
     * Serial + UI
     */
    private fun connect() {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(viewModel.selectedMachine.value!!.Mac)
            val deviceName =
                if (device.name != null) device.name else device.address
            status("connecting...")
            connected = Connected.Pending
            socket = SerialSocket()
            service.connect(this, "Connected to $deviceName")
            socket.connect(context, service, device)
        } catch (e: java.lang.Exception) {
            onSerialConnectError(e)
        }
    }


    private fun disconnect() {
        connected = Connected.False
        service.disconnect()
        socket.disconnect()
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val spn = SpannableStringBuilder(str + '\n')
            spn.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorSendText)),
                0,
                spn.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
//            receiveText.append(spn)
            val data = str.toByteArray()
            socket.write(data)
        } catch (e: Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(data: ByteArray) {
//        receiveText.append(String(data))
    }

    private fun status(str: String) {
        val spn = SpannableStringBuilder(str + '\n')
        spn.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorStatusText)),
            0,
            spn.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
//        receiveText.append(spn)
    }

    /*
     * SerialListener
     */
    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
    }

    override fun onSerialConnectError(e: Exception) {
        status("connection failed: " + e.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray) {
        Log.i("Debug", "read test : " + String(data))
        receive(data)
    }

    override fun onSerialIoError(e: Exception) {
        status("connection lost: " + e.message)
        disconnect()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
