package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemList
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.network.VendxApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {

    lateinit var machineItems: List<Item>

    private val _allGroupItems: MutableLiveData<ArrayList<ItemGroupModel>>
    val allGroupItems: LiveData<ArrayList<ItemGroupModel>>
        get() = _allGroupItems

    init {
        Log.i(TAG, "HomeViewModel created!")

        _allGroupItems = MutableLiveData()

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
                Log.i(TAG, "Successful to get response: ${response.body()} ")

                val moshi: Moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

                val jsonAdapter = moshi.adapter(ItemList::class.java)
                machineItems = jsonAdapter.fromJson(response.body())!!.items

                var itemGroupModel = ItemGroupModel(
                    items = machineItems,
                    draw_line_breaker = false
                )

                val temp = ArrayList<ItemGroupModel>()
                temp.add(itemGroupModel)
                _allGroupItems.value = temp
            }
        })
    }
}