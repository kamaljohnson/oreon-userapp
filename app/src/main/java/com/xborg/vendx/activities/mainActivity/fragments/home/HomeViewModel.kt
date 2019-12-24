package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.models.ItemModel
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {

    // firebase references
    private val db = FirebaseFirestore.getInstance()

    var machineItems: ArrayList<ItemModel>

    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    val allGroupItems: LiveData<ArrayList<ItemGroupModel>>
        get() = _allGroupItems

    init {
        Log.i(TAG, "HomeViewModel created!")

        _allGroupItems = MutableLiveData()

        machineItems = ArrayList()

        getItems()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
    }

    private fun getItems(machineId: String = "") {

        VendxApi.retrofitServices.getItems().enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.i(TAG, "Successful to get response: " + response.body())
            }

        })

//    region Depricated Code  [uses old implementation for accessing data]
//        db.collection("Inventory")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//
//                    val item = ItemModel(
//                        item_id = document.id,
//                        name = document.data["Name"].toString(),
//                        cost = document.data["Cost"].toString(),
//                        quantity = document.data["Quantity"].toString(),
//                        item_limit = "0",
//                        image_src = document.data["Image"].toString()
//                    )
//
//                    when (document.data["Category"].toString()) {
//                        "Snack" -> {
//                            item.category = ItemCategory.SNACK
//                        }
//                        "Beverage" -> {
//                            item.category = ItemCategory.BEVERAGE
//                        }
//                        "Fast Food" -> {
//                            item.category = ItemCategory.FAST_FOOD
//                        }
//                        else -> {
//                            item.category = ItemCategory.OTHER
//                        }
//                    }
//                    machineItems.add(item)
//                }
//
//                val machineInventoryItems = ItemGroupModel(items = machineItems, draw_line_breaker = false)
//                val tempAllGroupItems: ArrayList<ItemGroupModel> = ArrayList()
//                tempAllGroupItems.add(machineInventoryItems)
//                _allGroupItems.value = tempAllGroupItems
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
//    endregion
    }
}