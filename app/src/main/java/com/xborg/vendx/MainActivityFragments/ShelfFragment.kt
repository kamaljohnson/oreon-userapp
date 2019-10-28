package com.xborg.vendx.MainActivityFragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import kotlinx.android.synthetic.main.activity_shelf.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.rv_items_list

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    val items: ArrayList<Item> = ArrayList()               //all the items in the inventory list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getShelfItems()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_shelf, container, false)

    private fun getShelfItems(){
        MainActivity.items.clear()
        MainActivity.cart_items.clear()

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf")  == null) {
                    Log.d(TAG, "shelf is empty")
                    Toast.makeText(context, "Your Shelf is Empty", Toast.LENGTH_SHORT).show()
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
     * as item cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
        rv_items_list.layoutManager = LinearLayoutManager(context)
        rv_items_list.layoutManager = GridLayoutManager(context, 2)
        rv_items_list.adapter = context?.let { ItemAdapter(items, it) }
    }
}