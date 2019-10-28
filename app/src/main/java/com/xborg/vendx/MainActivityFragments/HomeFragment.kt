package com.xborg.vendx.MainActivityFragments
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getItems()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)


    /**
     * get all the items in the inventory
     */
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
                    item.item_limit = "0"
                    item.image_src = document.data["Image"].toString()

                    MainActivity.items.add(item)

                }
                addItemsToRV(MainActivity.items)

            }
            .addOnFailureListener { exception ->
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