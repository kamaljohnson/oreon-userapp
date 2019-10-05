package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.inventory_item.*

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

                    items.add(item)
                }
                rv_items_list.layoutManager = LinearLayoutManager(this)
                rv_items_list.layoutManager = GridLayoutManager(this, 1)
                rv_items_list.adapter = ItemAdapter(items, this)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}
