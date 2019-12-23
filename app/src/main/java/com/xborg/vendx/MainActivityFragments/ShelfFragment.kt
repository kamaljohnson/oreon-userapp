package com.xborg.vendx.MainActivityFragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.SupportClasses.ItemAdapter
import com.xborg.vendx.MainActivity
import com.xborg.vendx.Models.ItemModel
import com.xborg.vendx.R
import com.xborg.vendx.States
import kotlinx.android.synthetic.main.fragment_shelf.*

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    val items: ArrayList<ItemModel> = ArrayList()               //all the items in the inventory list
    var temp_items: ArrayList<ItemModel> = ArrayList()

    var is_visible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity = activity as MainActivity?
//        activity?.search_text?.addTextChangedListener{
//            Log.e(TAG, "the searching string is ${it.toString()}")
//            if(it.toString().isNotEmpty()) {
//                search(it.toString())
//            } else {
//                rv_inventory_snacks.removeAllViews()
//                addItemsToRV(items)
//            }
//
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shelf,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getShelfItems()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            is_visible = true
            val activity = activity as MainActivity?
        } else {
            is_visible = false
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "user_state: ${MainActivity.user_state}")
        when(MainActivity.user_state) {
            States.PAY_SUCCESS -> {
                rv_shelf_items.removeAllViews()
                getShelfItems()
                MainActivity.user_state = States.NEW_SELECT
            }
        }
    }

    private fun getShelfItems() {
        if(HomeFragment.shelf_items.count() == 0) {
            shelf_empty_container.visibility = View.VISIBLE
        } else {
            addItemsToRV(HomeFragment.shelf_items)
        }
    }

    /**
     * adds all the items to the recycler view
     * as item_card cards
     */
    private fun addItemsToRV(items: ArrayList<ItemModel>){
        Log.e(TAG, "items: $items")
        Log.e(TAG, "rv: $rv_shelf_items")
        rv_shelf_items.layoutManager = GridLayoutManager(context, 3)
        rv_shelf_items.adapter = context?.let { ItemAdapter(items) }
    }

    //    region item_card search

    private fun search(search_name: String) {
        temp_items = ArrayList()
        for (item in items) {
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
        rv_shelf_items.removeAllViews()
        addItemsToRV(temp_items)
    }

//    endregion
}