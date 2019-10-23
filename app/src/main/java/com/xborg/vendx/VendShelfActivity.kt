package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_vend_shelf.*

class VendShelfActivity : AppCompatActivity() {

    /*val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()
    private var TAG = "VendShelfActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vend_shelf)

        getShelfItems()
    }

    private fun getShelfItems(){
        InventoryActivity.items.clear()
        InventoryActivity.cart_items.clear()

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { machineSnap ->
                var shelfItems: Map<String, Number> = machineSnap.data?.get("Shelf") as Map<String, Number>
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

                            InventoryActivity.items.add(item)
                            if(InventoryActivity.items.size == shelfItems.size) {
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

    private fun addItemsToRV(){
        rv_items_list.layoutManager = LinearLayoutManager(this)
        rv_items_list.layoutManager = GridLayoutManager(this, 1)
        rv_items_list.adapter = ItemAdapter(InventoryActivity.items, this)
    }*/
}
