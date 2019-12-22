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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.MainActivity
import com.xborg.vendx.Models.ItemCategory
import com.xborg.vendx.Models.ItemGroupModel
import com.xborg.vendx.Models.ItemModel
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.ItemGroupAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    val allMachineItems: ArrayList<ItemModel> = ArrayList()               //all the items in the inventory list
    var tempItems: ArrayList<ItemModel> = ArrayList()

    var is_visible: Boolean = true

    companion object {
        lateinit var shelf_items: ArrayList<ItemModel>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate")

        shelf_items = ArrayList()

        MainActivity.items = allMachineItems
        val activity = activity as MainActivity?

//        activity?.search_text?.addTextChangedListener{
//            Log.e(TAG, "the searching string is ${it.toString()}")
//            if(it.toString().isNotEmpty()) {
//                search(it.toString())
//            } else {
//                rv_machine_inventory.removeAllViews()
//                addItemsToRV(items)
//            }
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getItems()
    }

    /**
     * get all the items in the inventory
     */
    private fun getItems() {
        MainActivity.items.clear()
        MainActivity.cart_items.clear()

        val itemGroups: ArrayList<ItemGroupModel> = ArrayList()

        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val item = ItemModel(
                        item_id = document.id,
                        name = document.data["Name"].toString(),
                        cost = document.data["Cost"].toString(),
                        quantity = document.data["Quantity"].toString(),
                        item_limit = "0",
                        image_src = document.data["Image"].toString()
                    )

                    when(document.data["Category"].toString()) {
                        "Snack" -> {
                            item.category = ItemCategory.SNACK
                        }
                        "Beverage" -> {
                            item.category = ItemCategory.BEVERAGE
                        }
                        "Fast Food" -> {
                            item.category = ItemCategory.FAST_FOOD
                        }
                        "Stationary" -> {
                            item.category = ItemCategory.STATIONARY
                        }
                        else -> {
                            item.category = ItemCategory.OTHER
                        }
                    }
                    allMachineItems.add(item)
                }

                val machineInventoryItems = ItemGroupModel(title = "", items = allMachineItems, draw_line_breaker = false)

                addItemsToRV(itemGroups)
                db.document("Users/$uid")
                    .get()
                    .addOnSuccessListener { userSnap ->
                        if(userSnap.data?.get("Shelf")  == null) {

                        } else {
                            var shelfItems: Map<String, Number> = userSnap.data?.get("Shelf") as Map<String, Number>

                            for (shelfItem in shelfItems){
                                Log.d(TAG, shelfItem.key)
                                Log.d(TAG, shelfItem.value.toString())

                                var itemId = shelfItem.key
                                var quantity = shelfItem.value

                                var machineItemsFromShelf: ArrayList<ItemModel> = ArrayList()

                                db.document("Inventory/${itemId}")
                                    .get()
                                    .addOnSuccessListener { document ->
                                        Log.d(TAG, "${document.id} => ${document.data}")

                                        val item = ItemModel()

                                        item.item_id = document.id
                                        item.name = document.data?.get("Name").toString()
                                        item.quantity = document.data?.get("Quantity").toString()
                                        item.cost = "-1"    // shelf items are already bought, no need to show the cost
                                        item.item_limit = quantity.toString()
                                        item.image_src = document.data?.get("Image").toString()

                                        shelf_items.add(item)

                                        if(shelfItems.size == shelf_items.size) {

                                            shelf_items.forEach { sItem ->
                                                allMachineItems.forEach { mItem ->
                                                    if (sItem.item_id == mItem.item_id) {
                                                        machineItemsFromShelf.add(sItem)
                                                    }
                                                }
                                            }

                                            if(machineItemsFromShelf.size != 0) {
                                                val itemsFromShelf = ItemGroupModel(title = "From Shelf", items = machineItemsFromShelf, draw_line_breaker = true)
                                                itemGroups.add(itemsFromShelf)
                                            }

                                            itemGroups.add(machineInventoryItems)
                                            addItemsToRV(itemGroups)
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
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun getShelfItems() {
        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf")  == null) {

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

                                val item = ItemModel()

                                item.item_id = document.id
                                item.name = document.data?.get("Name").toString()
                                item.quantity = document.data?.get("Quantity").toString()
                                item.cost = "-1"    // shelf items are already bought, no need to show the cost
                                item.item_limit = quantity.toString()
                                item.image_src = document.data?.get("Image").toString()
                                item.selectable = false

                                allMachineItems.add(item)

                                shelf_items = allMachineItems

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
    private fun addItemsToRV(itemGroups: ArrayList<ItemGroupModel>) {
        rv_machine_items.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_machine_items.adapter = context?.let { ItemGroupAdapter(itemGroups) }
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

//    region item_card search

    private fun search(search_name: String) {
        tempItems = ArrayList()
        for (item in allMachineItems) {
            Log.d(TAG, item.toString())
            var i = 0
            var j = 0
            while(i < item.name.length) {
                if(item.name[i].toUpperCase() == search_name[j].toUpperCase()) {
                    j++
                    if(j == search_name.length) {
                        tempItems.add(item)
                        break
                    }
                }
                i++
            }
            Log.d(TAG, tempItems.size.toString())
        }
        Log.e(TAG, MainActivity.cart_items.toString())
        rv_machine_items.removeAllViews()
//        addItemsToRV(temp_items)
    }

//    endregion
}