package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.models.ItemModel
import com.xborg.vendx.models.item.ItemCategory

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {

    // firebase references
    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().uid.toString()

    var allMachineItems: ArrayList<ItemModel>
    var allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    private var shelfItems: ArrayList<ItemModel>

    init {
        Log.i(TAG, "HomeViewModel created!")

        allMachineItems = ArrayList()
        allGroupItems = MutableLiveData()
        shelfItems = ArrayList()
        getItems()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
    }

    /**
     * get all the items in the inventory
     */
    private fun getItems() {

        var tempAllGroupItems: ArrayList<ItemGroupModel> = ArrayList()

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

                    when (document.data["Category"].toString()) {
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

                val machineInventoryItems = ItemGroupModel(items = allMachineItems, draw_line_breaker = false)

                db.document("Users/$uid")
                    .get()
                    .addOnSuccessListener { userSnap ->
                        if (userSnap.data?.get("Shelf") == null) {

                        } else {
                            val shelfItemsSnap: Map<String, Number> =
                                userSnap.data?.get("Shelf") as Map<String, Number>

                            if (shelfItemsSnap.isEmpty()) {
                                tempAllGroupItems.add(machineInventoryItems)
                                allGroupItems.value = tempAllGroupItems
                            }

                            for (shelfItem in shelfItemsSnap) {
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
                                        item.cost =
                                            "-1"    // shelf items are already bought, no need to show the cost
                                        item.item_limit = quantity.toString()
                                        item.image_src = document.data?.get("Image").toString()

                                        shelfItems.add(item)

                                        if (shelfItemsSnap.size == shelfItems.size) {

                                            shelfItems.forEach { sItem ->
                                                allMachineItems.forEach { mItem ->
                                                    if (sItem.item_id == mItem.item_id) {
                                                        machineItemsFromShelf.add(sItem)
                                                    }
                                                }
                                            }

                                            if (machineItemsFromShelf.size != 0) {
                                                val itemsFromShelf = ItemGroupModel(
                                                    items = machineItemsFromShelf,
                                                    draw_line_breaker = true
                                                )
                                                tempAllGroupItems.add(itemsFromShelf)
                                            }

                                            tempAllGroupItems.add(machineInventoryItems)
                                            allGroupItems.value = tempAllGroupItems
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(TAG, "Error getting documents.", exception)
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}