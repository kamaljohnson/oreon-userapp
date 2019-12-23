package com.xborg.vendx.activities.mainActivity.fragments.shelf

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
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.models.ItemModel
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.States
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_shelf.*

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    val items: ArrayList<ItemModel> = ArrayList()               //all the items in the inventory list

    var is_visible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        is_visible = isVisibleToUser
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

}