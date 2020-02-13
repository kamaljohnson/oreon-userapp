package com.xborg.vendx.activities.paymentActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.database.*
import java.io.Serializable
import java.lang.reflect.Type

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    var machineItems = MutableLiveData<List<Item>>()
    var inventoryItems = MutableLiveData<List<Item>>()

    // [itemId-from, count]     : from -> {Machine, Inventory}
    private var _cartItems = MutableLiveData<List<Item>>()
    val cartItems: LiveData<List<Item>>
        get() = _cartItems

    val order = MutableLiveData<Order>()
    val payment = MutableLiveData<Payment>()
    val paymentState = MutableLiveData<PaymentState>()

    val apiCallError = MutableLiveData<Boolean>()
    val apiCallRetryCount = MutableLiveData<Int>()

    init {
        apiCallRetryCount.value = 0
        paymentState.value = PaymentState.None
        payment.value = Payment(Id = "", OrderId = "")
        order.value = Order(Id = "")
    }

    fun updatePaymentAfterMakingPayment(status: PaymentStatus, razorpayPaymentID: String?) {
        payment.value!!.Status = if (status == PaymentStatus.SuccessfulLocal) {
            PaymentStatus.Processing
        } else {
            status
        }
        payment.value!!.RazorpayPaymentId = razorpayPaymentID ?: "not created, payment failed"

        paymentState.value = PaymentState.PaymentDone
    }

    private fun initOrder(cart: MutableMap<String, Int>, billingCart: MutableMap<String, Int>) {
        order.value!!.Uid = uid
        order.value!!.Cart = cart
        order.value!!.BillingCart = billingCart

        paymentState.value = PaymentState.OrderInit
    }

    fun setCartItemsFromSerializableHashMap(cartItemsAsHash: Serializable) {

        val tempCartMap = cartItemsAsHash as MutableMap<String, Int>
        val tempCartList = arrayListOf<Item>()

        var tempOrderCartMap = mutableMapOf<String, Int>()
        var tempOrderBillingCartMap = mutableMapOf<String, Int>()

        for ((sudoId, count) in tempCartMap) {
            val from = sudoId.split('/')[0]
            val id = sudoId.split('/')[1]

            if(tempOrderCartMap.containsKey(id)) {
                tempOrderCartMap[id] = tempOrderCartMap[id]!!  + count
            } else {
                tempOrderCartMap[id] = count
            }

            when (from) {
                "Machine" -> {
                    machineItems.value!!.forEach { item ->
                        if (item.Id == id) {
                            Log.i(TAG, "from: $from id: $id")
                            item.cartCount = count
                            tempCartList.add(item)
                            tempOrderBillingCartMap[id] = count
                        } else {
                            Log.i(TAG, item.Id)
                        }
                    }
                }
                "Inventory" -> {
                    inventoryItems.value!!.forEach { item ->
                        if (item.Id == id) {
                            Log.i(TAG, "from: $from id: $id")
                            item.cartCount = count
                            tempCartList.add(item)
                        } else {
                            Log.i(TAG, item.Id)
                        }
                    }
                }
            }
        }

        _cartItems.value = tempCartList
        initOrder(tempOrderCartMap, tempOrderBillingCartMap)
    }

    fun setMachineItemsFromSerializable(machineItemsAsJson: Serializable) {
        machineItems.value = convertJsonToItemList(machineItemsAsJson as String)
    }

    fun setInventoryItemsFromSerializable(inventoryItemsAsJson: Serializable) {
        inventoryItems.value = convertJsonToItemList(inventoryItemsAsJson as String)
    }

    private fun convertJsonToItemList(json: String): List<Item> {
        val itemListType: Type = object : TypeToken<ArrayList<Item?>?>() {}.type
        return Gson().fromJson(json, itemListType)
    }

    fun getCartItemsAsJson(): String {
        return getListItemsAsJson(_cartItems.value!!)
    }

    fun getMachineItemsAsJson(): String {
        return getListItemsAsJson(machineItems.value!!)
    }

    fun getInventoryItemsAsJson(): String {
        return getListItemsAsJson(inventoryItems.value!!)
    }

    private fun getListItemsAsJson(items: List<Item>): String {
        val itemListType: Type = object : TypeToken<ArrayList<Item?>?>() {}.type
        return Gson().toJson(items, itemListType)
    }
}