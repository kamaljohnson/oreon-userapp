package com.xborg.vendx

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.adapters.ItemSlipAdapter
import kotlinx.android.synthetic.main.activity_vending.*
import java.util.*
import kotlin.collections.HashMap

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus
import com.xborg.vendx.models.ItemModel

private var TAG = "VendingActivity"

@Suppress("CAST_NEVER_SUCCEEDS")
class VendingActivity : AppCompatActivity(), BluetoothService.OnBluetoothEventCallback{
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

//    val db = FirebaseFirestore.getInstance()
//    val uid =  FirebaseAuth.getInstance().uid.toString()
//    var bag_items : HashMap<String, Int> = HashMap()
//
//    var vendID = ""
//
//    private lateinit var mBluetoothAdapter: BluetoothAdapter
//    private lateinit var mService: BluetoothService
//    private var mScanning: Boolean = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_vending)
//        setupBluetooth()
//        getBag()
//        send_to_device.setOnClickListener {
//            mService.write(to_device_text.text.toString().toByteArray())
//        }
//        send_to_server.setOnClickListener {
//            when {
//                to_server_text.text.toString() == "" -> Toast.makeText(this, "the test text is empty", Toast.LENGTH_SHORT).show()
//                to_server_text.text.toString() == "rqt" -> {
//                    val vend = HashMap<String, Any>()
//                    vend["UID"] = FirebaseAuth.getInstance().uid.toString()
//                    vend["MID"] = "yDWzDc79Uu1IO2lEeVyG"    //TODO: use the actual MID
//                    vend["Status"] = "Request Created"
//
//                    db.collection("Vends")
//                        .add(vend)
//                        .addOnSuccessListener { vendRef ->
//                            Log.d(TAG, "vendReference created with ID: ${vendRef.id}")
//                            vendID = vendRef.id
//
//                        }
//                        .addOnFailureListener{
//                            Log.d(TAG, "Failed to place vend")
//                        }
//                }
//                else -> {
//                    val vend = HashMap<String, Any>()
//                    vend["Status"] = "Message Received"
//                    vend["Msg"] = to_server_text.text.toString()
//
//                    db.document("Vends/$vendID")
//                        .update(vend)
//                        .addOnSuccessListener {
//
//                        }
//                        .addOnFailureListener{
//                            Log.d(TAG, "Failed to place vend")
//                        }
//                }
//            }
//        }
//    }
//
//    private fun getBag() {
//
//        db.document("Users/$uid")
//            .get()
//            .addOnSuccessListener { userSnap ->
//                if(userSnap.data?.get("Shelf")  == null) {
//                    Log.d(TAG, "shelf is empty")
//                    Toast.makeText(this, "Your Shelf is Empty", Toast.LENGTH_SHORT).show()
//                } else {
//                    var shelfItems: Map<String, Number> = userSnap.data?.get("Bag") as Map<String, Number>
//                    for (shelfItem in shelfItems){
//                        Log.d(TAG, shelfItem.key)
//                        Log.d(TAG, shelfItem.value.toString())
//
//                        var itemId = shelfItem.key
//                        var quantity = shelfItem.value
//
//                        db.document("Inventory/${itemId}")
//                            .get()
//                            .addOnSuccessListener { document ->
//                                Log.d(TAG, "${document.id} => ${document.data}")
//
//                                val item = ItemModel()
//
//                                item.item_id = document.id
//                                item.name = document.data?.get("Name").toString()
//                                item.quantity = document.data?.get("Quantity").toString()
//                                item.cost = "-1"    // shelf items are already bought, no need to show the cost
//                                item.item_limit = quantity.toString()
//                                item.image_src = document.data?.get("Image").toString()
//
//                                bag_items[item.item_id] = quantity.toInt()
//                                Log.d(TAG, item.item_id + " -> " + quantity)
//
//                            }
//                            .addOnFailureListener{exception ->
//                                Log.w(TAG, "Error getting documents.", exception)
//                            }
//                    }
//                }
//            }
//            .addOnFailureListener {exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
//    }
//
//    /**
//     * adds all the items to the recycler view
//     * as item_card cards
//     */
//    private fun addItemsToRV(items: ArrayList<ItemModel>){
//        rv_shelf_items.layoutManager = LinearLayoutManager(this)
//        rv_shelf_items.layoutManager = GridLayoutManager(this, 1)
//        rv_shelf_items.adapter = ItemSlipAdapter(items, this)
//    }
//
//    private fun setupBluetooth(){
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//
//
//        mService = BluetoothService.getDefaultInstance()
//        mService.setOnEventCallback(this)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mService.setOnEventCallback(this)
//    }
//
//    override fun onDataRead(buffer: ByteArray?, length: Int) {
//        Log.d(TAG, "onDataRead : $buffer")
//        if (buffer != null) {
//            to_server_text.text = buffer.toString()
//        }
//    }
//
//    override fun onDataWrite(buffer: ByteArray?) {
//        Log.d(TAG, "onDataWrite")
//    }
//
//    override fun onStatusChange(status: BluetoothStatus?) {
//        Log.d(TAG, "onStatusChange: $status")
//        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show()
//
//        if (status == BluetoothStatus.CONNECTED) {
//            Log.d(TAG, "device connected")
//        }
//    }
//
//    override fun onToast(message: String?) {
//        Log.d(TAG, "onToast")
//    }
//
//    override fun onDeviceName(deviceName: String?) {
//        Log.d(TAG, "onDeviceName: $deviceName")
//    }
}