package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity.Companion.items
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import com.xborg.vendx.SupportClasses.ItemSlipAdapter
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "VendingActivity"

class VendingActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()
    var bag_items : HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        getBag()
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
