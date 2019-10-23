package com.xborg.vendx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_shelf.*

class ShelfActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()
    private var TAG = "ShelfActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)

        getShelfItems()

        get_button.setOnClickListener{
            val order = HashMap<String, Any>()
            order["UID"] = FirebaseAuth.getInstance().uid.toString()
            order["Cart"] = HomeActivity.billing_cart

            HomeActivity.billing_cart.clear()
            HomeActivity.cart_items_from_shelf = HomeActivity.cart_items

            order["Status"] = "From Shelf"

            db.collection("Orders")
                .add(order)
                .addOnSuccessListener { orderRef ->
                    Log.d(TAG, "billReference created with ID: ${orderRef.id}")

                    val order_id = orderRef.id

                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("order_id", order_id)
                    intent.putExtra("cart_items", HomeActivity.cart_items)
                    intent.putExtra("billing_cart", HomeActivity.billing_cart)
                    startActivity(intent)

                }
                .addOnFailureListener{
                    Log.d(TAG, "Failed to place order")
                }
        }
    }

    private fun getShelfItems(){
        HomeActivity.items.clear()
        HomeActivity.cart_items.clear()

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                var shelfItems: Map<String, Number> = userSnap.data?.get("Shelf") as Map<String, Number>
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

                            HomeActivity.items.add(item)
                            if(HomeActivity.items.size == shelfItems.size) {
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
        rv_items_list.layoutManager = GridLayoutManager(this, 2)
        rv_items_list.adapter = ItemAdapter(HomeActivity.items, this)
    }
}
