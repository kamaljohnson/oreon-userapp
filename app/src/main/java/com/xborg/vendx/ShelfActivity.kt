package com.xborg.vendx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        clearCarts()
        getShelfItems()

        get_button.setOnClickListener{

            if(HomeActivity.cart_items.size == 0) {
                Toast.makeText(this, "Your Cart is Empty", Toast.LENGTH_SHORT).show()
            } else {

                val intent = Intent(this, VendingActivity::class.java)
                intent.putExtra("cart_items", HomeActivity.cart_items)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        clearCarts()
        getShelfItems()

    }

    private fun getShelfItems(){
        HomeActivity.items.clear()
        HomeActivity.cart_items.clear()

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf")  == null) {
                    Log.d(TAG, "shelf is empty")
                    Toast.makeText(this, "Your Shelf is Empty", Toast.LENGTH_SHORT).show()
                } else {
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

    private fun clearCarts() {
        HomeActivity.items.clear()
        HomeActivity.shelf_items.clear()
        HomeActivity.cart_items.clear()
        HomeActivity.cart_items_from_shelf.clear()
        HomeActivity.billing_cart.clear()
    }

}
