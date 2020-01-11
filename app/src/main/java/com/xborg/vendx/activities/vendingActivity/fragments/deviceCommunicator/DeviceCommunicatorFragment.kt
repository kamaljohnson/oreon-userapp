package com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Base64
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
import com.xborg.vendx.database.VendingState
import com.xborg.vendx.database.VendingStatus

const val TAG = "DeviceCommunicator"

class DeviceCommunicatorFragment : Fragment(), ServiceConnection, SerialListener {

    private enum class Connected {
        False, Pending, True
    }

    private var deviceAddress: String? = null

    private var socket: SerialSocket? = null
    private var service: SerialService? = null

    private var initialStart = true
    private var connected: Connected = Connected.False

    private lateinit var viewModel: DeviceCommunicatorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceAddress = "3C:71:BF:79:86:22"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_device_communicator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DeviceCommunicatorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.bagState.observe(this, Observer { updatedBagStatus ->
            if (viewModel.bagState.value!! < updatedBagStatus) {

                viewModel.bagState.value = updatedBagStatus
                viewModel.bag.value = sharedViewModel.bag.value

                when (updatedBagStatus) {
                    VendingState.DeviceConnected -> {
                        requestOtpFromDevice()
                    }
                    VendingState.EncryptedOtpPlusBagReceived -> {
                        sendEncryptedOtpPlusBagToDevice()
                    }
                    VendingState.Vending -> TODO()
                    VendingState.VendingDone -> TODO()
                }
            }
        })

        viewModel.bagState.observe(this, Observer { updatedBagStatus ->
            if (sharedViewModel.bagState.value!! < updatedBagStatus) {

                sharedViewModel.bagState.value = updatedBagStatus
                sharedViewModel.bag.value = viewModel.bag.value
            }
        })

//        send_to_device_button.setOnClickListener {
//            send(to_device_text.text.toString())
//        }
    }

    override fun onStart() {
        super.onStart()
        if (service != null) {
            service!!.attach(this)
        } else {
            activity!!.startService(
                Intent(
                    activity,
                    SerialService::class.java
                )
            ) // prevents service destroy on unbind from recreated activity caused by orientation change
        }
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        activity!!.stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    override fun onStop() {
        if (service != null && !activity!!.isChangingConfigurations) service!!.detach()
        super.onStop()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity!!.bindService(
            Intent(context, SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
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
            activity!!.runOnUiThread(Runnable { this.connect() })
        }
    }

    private fun connect() {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            val deviceName =
                if (device.name != null) device.name else device.address
            status("connecting...")
            connected = Connected.Pending
            socket = SerialSocket()
            service!!.connect(this, "Connected to $deviceName")
            socket!!.connect(context, service, device)
        } catch (e: java.lang.Exception) {
            onSerialConnectError(e)
        }
    }

    private fun disconnect() {
        connected = Connected.False
        service!!.disconnect()
        socket!!.disconnect()
        socket = null
    }

    private fun requestOtpFromDevice() {
        send("vendx_init_transaction")
    }

    private fun sendEncryptedOtpPlusBagToDevice() {
        send(viewModel.bag.value!!.encryptedOtpPlusBag, true)
    }

    private fun send(str: String, isBase64: Boolean = false) {
        Log.i(TAG, "sending : $str")
        if (connected != Connected.True) {
            Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val data: ByteArray = if (isBase64) {
                Base64.decode(str, Base64.NO_WRAP)
            } else {
                str.toByteArray()
            }
            socket!!.write(data)
        } catch (e: java.lang.Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(dataFromDevice: ByteArray) {

        val dataStr = String(dataFromDevice)
        val dataFromDeviceBase64 = Base64.encodeToString(dataFromDevice, Base64.NO_WRAP)
        Log.i(TAG, "received : $dataStr : $dataFromDeviceBase64")

        if (dataStr == "OTP_TIMEOUT") {     //otp timed out and required to be re-requested
            requestOtpFromDevice()
        } else if (dataStr == "VENDING") {   //a single vend is completed by device
            sharedViewModel.bagState.value = VendingState.Vending
            sharedViewModel.updateVendingCount()
        } else if (dataStr == "VEND_DONE") {
            sharedViewModel.bagState.value = VendingState.VendingDone
            sharedViewModel.bag.value!!.status = VendingStatus.Done
        } else {
            when (viewModel.bagState.value!!) {
                VendingState.DeviceConnected -> viewModel.addEncryptedOtp(dataFromDeviceBase64)
                VendingState.EncryptedOtpReceived -> {  //TODO: check validity of the otp

                }
                VendingState.VendingDone -> viewModel.addEncryptedLog(dataFromDeviceBase64)
            }
        }
    }

    private fun status(str: String) {
        Log.i(TAG, "connection status : $str")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as SerialService.SerialBinder).service

        if (initialStart) {
            initialStart = false
            activity!!.runOnUiThread(Runnable { connect() })
        }
    }

    override fun onSerialIoError(e: Exception?) {
        Log.i(TAG, "onSerialIoError: " + e!!.message)
        status("connection lost: " + e.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray?) {
        receive(data!!)
    }

    override fun onSerialConnectError(e: Exception?) {
        Log.i(TAG, "onSerialConnectError: " + e!!.message)
        status("connection failed: " + e.message)
        disconnect()
    }

    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
        sharedViewModel.bagState.value = VendingState.DeviceConnected
    }
}
