package com.xborg.vendx.activities.vendingActivity.fragments.communicators.device

import android.bluetooth.BluetoothAdapter
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
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.fragment_device_communicator.*

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

        send_to_device_button.setOnClickListener {
            send(to_device_text.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        if(service != null) {
            Log.i(TAG, "here >>>>>>>>> 1")
            service!!.attach(this)
        } else {
            Log.i(TAG, "here >>>>>>>>> 2")
            val result = activity!!.startService(Intent(activity, SerialService::class.java)) // prevents service destroy on unbind from recreated activity caused by orientation change
            Log.i(TAG, "activity : " + activity!!.toString())
            Log.i(TAG, "result : $result")
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
        activity!!.bindService(Intent(context, SerialService::class.java), this, Context.BIND_AUTO_CREATE)
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

    /*
     * Serial + UI
     */
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

    private fun send(str: String) {
        Log.i(TAG, "sending : $str")
        if (connected != Connected.True) {
            Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show()
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
            val data = (str).toByteArray()
            socket!!.write(data)
        } catch (e: java.lang.Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(data: ByteArray) {
        Log.i(TAG, "received : $data")
        text_from_device.text = data.toString()
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

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as SerialService.SerialBinder).service
        Log.i(TAG, "here >>>>>>>>> 3")

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
    }
}
