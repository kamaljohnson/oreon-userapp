package com.xborg.vendx.VendingActivityFragments

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import com.xborg.vendx.Bluetooth.BluetoothService
import com.xborg.vendx.Bluetooth.Constants
import com.xborg.vendx.R
import kotlin.math.log

class BluetoothFragment: Fragment() {

    private val TAG = "BluetoothFragment"

    // Intent request codes
    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_CONNECT_DEVICE_INSECURE = 2
    private val REQUEST_ENABLE_BT = 3

    private var mOutEditText: EditText? = null
    private var mSendButton: Button? = null

    /**
     * Name of the connected device
     */
    private var mConnectedDeviceName: String? = null

    /**
     * Array adapter for the conversation thread
     */
    private var mConversationArrayAdapter: ArrayAdapter<String>? = null

    /**
     * String buffer for outgoing messages
     */
    private var mOutStringBuffer: StringBuffer? = null

    /**
     * Local Bluetooth adapter
     */
    private var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * Member object for the chat services
     */
    private var mBluetoothService: BluetoothService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "Bluetooth fragment created")

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // If the adapter is null, then Bluetooth is not supported
        val activity = activity
        if (mBluetoothAdapter == null && activity != null) {
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show()
            activity.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mBluetoothAdapter == null) {
            return
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter!!.isEnabled()) {
            mBluetoothAdapter!!.enable()
            setupChat()
            // Otherwise, setup the chat session
        } else if (mBluetoothService == null) {
            setupChat()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mOutEditText = view.findViewById(R.id.to_server_text)
        mSendButton = view.findViewById(R.id.send_to_device)
        Log.e(TAG, "connected mSendButton to send_to_device")
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private fun setupChat() {

        Log.e(TAG, "calling setupChat")

        // Initialize the compose field with a listener for the return key
        mOutEditText?.setOnEditorActionListener(mWriteListener)

        // Initialize the send button with a listener that for click events
        mSendButton?.setOnClickListener {
            // Send a message using content of the edit text widget
            val view = view
            if (null != view) {
                val textView = view.findViewById<TextView>(R.id.to_device_text)
                val message = textView.text.toString()
                sendMessage(message)
            }
        }

        // Initialize the BluetoothChatService to perform bluetooth connections

        mBluetoothService = BluetoothService(activity, mHandler)

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = StringBuffer()

        connectDevice(true)
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private fun sendMessage(message: String) {
        Log.e(TAG, "sending message via bluetooth")
        // Check that we're actually connected before trying anything
        if (mBluetoothService?.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(activity, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mBluetoothService?.write(send)

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer?.setLength(0)
            mOutEditText?.setText(mOutStringBuffer)
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private val mWriteListener = TextView.OnEditorActionListener { view, actionId, event ->
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.action == KeyEvent.ACTION_UP) {
                val message = view.text.toString()
                sendMessage(message)
            }
            true
        }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private fun setStatus(resId: Int) {
        val activity = activity ?: return
        val actionBar = activity.actionBar ?: return
        actionBar.setSubtitle(resId)
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private fun setStatus(subTitle: CharSequence) {
        val activity = activity ?: return
        val actionBar = activity.actionBar ?: return
        actionBar.subtitle = subTitle
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            val activity = activity
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BluetoothService.STATE_CONNECTED -> {
                        setStatus(getString(R.string.title_connected_to, mConnectedDeviceName))
                        mConversationArrayAdapter?.clear()
                    }
                    BluetoothService.STATE_CONNECTING -> setStatus(R.string.title_connecting)
                    BluetoothService.STATE_LISTEN, BluetoothService.STATE_NONE -> setStatus(
                        R.string.title_not_connected
                    )
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    mConversationArrayAdapter?.add("Me:  $writeMessage")
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    mConversationArrayAdapter?.add("$mConnectedDeviceName:  $readMessage")
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    if (null != activity) {
                        Toast.makeText(
                            activity,
                            "Connected to $mConnectedDeviceName",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Constants.MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(
                        activity, msg.data.getString(Constants.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        when (requestCode) {
//            REQUEST_CONNECT_DEVICE_SECURE ->
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data!!, true)
//                }
//            REQUEST_CONNECT_DEVICE_INSECURE ->
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data!!, false)
//                }
//            REQUEST_ENABLE_BT ->
//                // When the request to enable Bluetooth returns
//                if (resultCode == Activity.RESULT_OK) {
//                    // Bluetooth is now enabled, so set up a chat session
//                    setupChat()
//                } else {
//                    // User did not enable Bluetooth or an error occurred
//                    Log.d(TAG, "BT not enabled")
//                    val activity = activity
//                    if (activity != null) {
//                        Toast.makeText(
//                            activity, R.string.bt_not_enabled_leaving,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        activity.finish()
//                    }
//                }
//        }
//    }

    /**
     * Establish connection with other device
     *
     * @param data   An [Intent] with [DeviceListActivity.EXTRA_DEVICE_ADDRESS] extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private fun connectDevice(secure: Boolean) {
        // Get the device MAC address
        //val address = "3C:71:BF:79:86:22" //ESP
        val address = "D8:5D:E2:C8:66:82" //DEXTER LAP
        Log.e(TAG, "tryping to connect to device with address: " + address)
        // Get the BluetoothDevice object
        val device = mBluetoothAdapter?.getRemoteDevice(address)
        // Attempt to connect to the device
        mBluetoothService?.connect(device, secure)
        Log.e(TAG, "paired with device : " + device.toString())
    }
}