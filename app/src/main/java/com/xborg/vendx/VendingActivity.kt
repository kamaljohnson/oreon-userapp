package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity.Companion.items
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemSlipAdapter
import com.xborg.vendx.VendingActivityFragments.BluetoothFragment
import kotlinx.android.synthetic.main.activity_vending.*
import kotlinx.android.synthetic.main.fragment_home.rv_items_list
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private var TAG = "VendingActivity"

class VendingActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()
    var bag_items : HashMap<String, Int> = HashMap()

    var vendID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(TAG, "inside vending activity -> 0")
        setContentView(R.layout.activity_vending)
        Log.e(TAG, "inside  creating vending activity -> 1")
        getBag()

        if (savedInstanceState == null) {
            Log.e(TAG, "inside creating vending activity -> 2")

            Toast.makeText(this, "creating bluetooth fragment", Toast.LENGTH_SHORT).show()
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = BluetoothFragment()
            transaction.replace(R.id.bluetooth_fragment, fragment)
            transaction.commit()
            Log.e(TAG, "inside creating vending activity -> 3")

        }

        send_to_server.setOnClickListener {
            when {
                to_server_text.text.toString() == "" -> Toast.makeText(this, "the test text is empty", Toast.LENGTH_SHORT).show()
                to_server_text.text.toString() == "rqt" -> {
                    val vend = HashMap<String, Any>()
                    vend["UID"] = FirebaseAuth.getInstance().uid.toString()
                    vend["MID"] = "yDWzDc79Uu1IO2lEeVyG"    //TODO: use the actual MID
                    vend["Status"] = "Request Created"

                    db.collection("Vends")
                        .add(vend)
                        .addOnSuccessListener { vendRef ->
                            Log.d(TAG, "vendReference created with ID: ${vendRef.id}")
                            vendID = vendRef.id

                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to place vend")
                        }
                }
                else -> {
                    val vend = HashMap<String, Any>()
                    vend["Status"] = "Message Received"
                    vend["Msg"] = to_server_text.text.toString()

                    db.document("Vends/$vendID")
                        .update(vend)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to place vend")
                        }
                }
            }
        }
    }

    private fun getBag() {
        items.clear()

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf")  == null) {
                    Log.d(TAG, "shelf is empty")
                    Toast.makeText(this, "Your Shelf is Empty", Toast.LENGTH_SHORT).show()
                } else {
                    var shelfItems: Map<String, Number> = userSnap.data?.get("Bag") as Map<String, Number>
                    for (shelfItem in shelfItems){
                        Log.d(TAG, shelfItem.key)
                        Log.d(TAG, shelfItem.value.toString())

                        var itemId = shelfItem.key
                        var quantity = shelfItem.value

                        db.document("Inventory/${itemId}")
                            .get()
                            .addOnSuccessListener { document ->
                                Log.d(TAG, "${document.id} => ${document.data}")

                                val item = Item()

                                item.item_id = document.id
                                item.name = document.data?.get("Name").toString()
                                item.quantity = document.data?.get("Quantity").toString()
                                item.cost = "-1"    // shelf items are already bought, no need to show the cost
                                item.item_limit = quantity.toString()
                                item.image_src = document.data?.get("Image").toString()

                                bag_items[item.item_id] = quantity.toInt()
                                Log.e(TAG, item.item_id + " -> " + quantity)
                                items.add(item)

                                if(items.size == shelfItems.size) {
                                    addItemsToRV(items)
                                }
                            }
                            .addOnFailureListener{exception ->
                                Log.w(TAG, "Error getting documents.", exception)
                            }
                    }
                }
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /**
     * adds all the items to the recycler view
     * as item_card cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
        rv_items_list.layoutManager = LinearLayoutManager(this)
        rv_items_list.layoutManager = GridLayoutManager(this, 1)
        rv_items_list.adapter = ItemSlipAdapter(items, this)
    }
}
