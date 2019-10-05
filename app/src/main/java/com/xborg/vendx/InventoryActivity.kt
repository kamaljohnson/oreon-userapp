package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlin.reflect.typeOf

class InventoryActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private var TAG = "InventoryActivity"

    val items: ArrayList<Item> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        var mid: String = intent.getStringExtra("mid")
        if(mid == "") {
            getAllItems()
        } else {
            getAllItems(mid)
        }
    }

    //get item inside machine mid
    private fun getAllItems(mid:String){
        db.document("Machines/$mid")
            .get()
            .addOnSuccessListener { machineSnap ->
                var inventoryArray:ArrayList<Map<String, String>> = machineSnap.data?.get("Inventory") as ArrayList<Map<String, String>>
                for (inventory in inventoryArray){
                    Log.d(TAG, inventory.getValue("Quantity").toString())
                    Log.d(TAG, inventory.getValue("Item").toString())

                    var itemRef = inventory.getValue("Item").toString()
                    var quantity = inventory.getValue("Quantity").toString()

                    db.document(itemRef)
                        .get()
                        .addOnSuccessListener { document ->
                            Log.d(TAG, "${document.id} => ${document.data}")

                            val item = Item()

                            item.name = document.data?.get("Name").toString()
                            item.cost = document.data?.get("Cost").toString()
                            item.quantity = document.data?.get("Quantity").toString()
                            item.items_left = quantity

                            items.add(item)
                            if(items.size == inventory.size) {
                                addItemsToRV()
                            }
                        }
                        .addOnFailureListener{exception ->
                            Log.w(TAG, "Error getting documents.", exception)
                        }
                }
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    //get all the items
    private fun getAllItems() {
        // [START get_all_users]
        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val item = Item()

                    item.name = document.data["Name"].toString()
                    item.cost = document.data["Cost"].toString()
                    item.quantity = document.data["Quantity"].toString()
                    item.items_left = ""

                    items.add(item)
                }

                addItemsToRV()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun addItemsToRV(){
        rv_items_list.layoutManager = LinearLayoutManager(this)
        rv_items_list.layoutManager = GridLayoutManager(this, 1)
        rv_items_list.adapter = ItemAdapter(items, this)
    }
}
