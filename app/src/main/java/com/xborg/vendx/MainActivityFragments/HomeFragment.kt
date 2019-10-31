package com.xborg.vendx.MainActivityFragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()

    val items: ArrayList<Item> = ArrayList()               //all the items in the inventory list
    var temp_items: ArrayList<Item> = ArrayList()

    var is_visible: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getItems()
        val activity = activity as MainActivity?
        activity?.search_text?.addTextChangedListener{
            Log.e(TAG, "the searching string is ${it.toString()}")
            if(it.toString().isNotEmpty()) {
                search(it.toString())
            } else {
                rv_items_list.removeAllViews()
                addItemsToRV(MainActivity.items)
            }
        }
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

                    items.add(item)

                }
                addItemsToRV(items)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }


    /**
     * adds all the items to the recycler view
     * as item_card cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
        rv_items_list.layoutManager = LinearLayoutManager(context)
        rv_items_list.layoutManager = GridLayoutManager(context, 2)
        rv_items_list.adapter = context?.let { ItemAdapter(items, it) }
    }

    @SuppressLint("ResourceAsColor")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            is_visible = true
            Log.e(TAG, "fragment Home is visible")
            val activity = activity as MainActivity?
            activity?.home_button?.setBackgroundResource(R.drawable.rounded_button_orange)
            activity?.shelf_button?.setBackgroundResource(R.color.fui_transparent)
        } else {
            is_visible = false
        }
    }

//    region item_card search

    private fun search(search_name: String) {
        temp_items = ArrayList()
        for (item in MainActivity.items) {
            Log.d(TAG, item.toString())
            var i = 0
            var j = 0
            while(i < item.name.length) {
                if(item.name[i].toUpperCase() == search_name[j].toUpperCase()) {
                    j++
                    if(j == search_name.length) {
                        temp_items.add(item)
                        break
                    }
                }
                i++
            }
            Log.d(TAG, temp_items.size.toString())
        }
        Log.e(TAG, MainActivity.cart_items.toString())
        rv_items_list.removeAllViews()
        addItemsToRV(temp_items)
    }

//    endregion
}