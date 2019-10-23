package com.xborg.vendx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_inventory.*

class InventoryActivity : AppCompatActivity() {

    /*val db = FirebaseFirestore.getInstance()
    private var TAG = "InventoryActivity"


    companion object{

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        var mid: String = intent.getStringExtra("mid")

        HomeActivity.items.clear()
        HomeActivity.shelf_items.clear()
        HomeActivity.cart_items.clear()
        HomeActivity.cart_items_from_shelf.clear()
        HomeActivity.billing_cart.clear()

        if(mid == "") {
            getItems()
        } else {
            getItems(mid)
        }

        buy_button.setOnClickListener{
            buy_button.visibility = View.INVISIBLE
            HomeActivity.cart_items.forEach{
                Log.d(TAG, it.key + " => " + it.value)
            }
            val order = HashMap<String, Any>()
            order["UID"] = FirebaseAuth.getInstance().uid.toString()
            order["Cart"] = HomeActivity.cart_items
            order["Status"] = "Payment Pending"


            db.collection("Orders")
                .add(order)
                .addOnSuccessListener { orderRef ->
                    Log.d(TAG, "billReference created with ID: ${orderRef.id}")

                    val order_id = orderRef.id
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("order_id", order_id)
                    startActivity(intent)

                }
                .addOnFailureListener{
                    Log.d(TAG, "Failed to place order")
                }
        }
    }

    //get item inside machine mid
    private fun getItems(mid:String){
        db.document("Machines/$mid")
            .get()
            .addOnSuccessListener { machineSnap ->
                var inventoryItems: Map<String, Number> = machineSnap.data?.get("Inventory") as Map<String, Number>
                for (inventoryItem in inventoryItems){
                    Log.d(TAG, inventoryItem.key)
                    Log.d(TAG, inventoryItem.value.toString())

                    var itemId = inventoryItem.key
                    var quantity = inventoryItem.value

                    db.document("Inventory/${itemId}")
                        .get()
                        .addOnSuccessListener { document ->
                            Log.d(TAG, "${document.id} => ${document.data}")

                            val item = Item()

                            item.item_id = document.id
                            item.name = document.data?.get("Name").toString()
                            item.cost = document.data?.get("Cost").toString()
                            item.quantity = document.data?.get("Quantity").toString()
                            item.item_limit = quantity.toString()

                            HomeActivity.items.add(item)
                            if(HomeActivity.items.size == inventoryItems.size) {
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
    private fun getItems() {
        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val item = Item()

                    item.item_id = document.id
                    item.name = document.data["Name"].toString()
                    item.cost = document.data["Cost"].toString()
                    item.quantity = document.data["Quantity"].toString()
                    item.item_limit = ""
                    HomeActivity.items.add(item)
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
        rv_items_list.adapter = ItemAdapter(HomeActivity.items, this)
    }*/
}
