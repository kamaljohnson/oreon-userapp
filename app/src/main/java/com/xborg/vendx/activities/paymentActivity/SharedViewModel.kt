package com.xborg.vendx.activities.paymentActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xborg.vendx.database.*
import java.io.Serializable
import java.lang.reflect.Type

class SharedViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid.toString()

    var machineItems = MutableLiveData<List<Item>>()
    var shelfItems = MutableLiveData<List<Item>>()

    // [itemId-from, count]     : from -> {Machine, Shelf}
    private var _cartItems = MutableLiveData<List<Item>>()
    val cartItems: LiveData<List<Item>>
        get() = _cartItems

    val order = MutableLiveData<Order>()
    val payment = MutableLiveData<Payment>()

    val paymentState = MutableLiveData<PaymentState>()

    init {
        paymentState.value = PaymentState.None
        payment.value = Payment(id = "", orderId = "")
        order.value = Order(id = "")
    }

    fun updatePaymentAfterMakingPayment(status: PaymentStatus, razorpayPaymentID: String?) {
        payment.value!!.status = if (status == PaymentStatus.SuccessfulLocal) {
            PaymentStatus.Processing
        } else {
            status
        }
        payment.value!!.razorpayPaymentId = razorpayPaymentID ?: "not created, payment failed"

        paymentState.value = PaymentState.PaymentDone
    }

    private fun initOrder(cart: MutableMap<String, Int>, billingCart: MutableMap<String, Int>) {
        order.value!!.uid = uid
        order.value!!.cart = cart
        order.value!!.billingCart = billingCart

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
                        if (item.id == id) {
                            Log.i(TAG, "from: $from id: $id")
                            item.cartCount = count
                            tempCartList.add(item)
                            tempOrderBillingCartMap[id] = count
                        } else {
                            Log.i(TAG, item.id)
                        }
                    }
                }
                "Shelf" -> {
                    shelfItems.value!!.forEach { item ->
                        if (item.id == id) {
                            Log.i(TAG, "from: $from id: $id")
                            item.cartCount = count
                            tempCartList.add(item)
                        } else {
                            Log.i(TAG, item.id)
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

    fun setShelfItemsFromSerializable(shelfItemsAsJson: Serializable) {
        shelfItems.value = convertJsonToItemList(shelfItemsAsJson as String)
    }

    private fun convertJsonToItemList(json: String): List<Item> {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val itemListDataType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Item::class.java
        )
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(itemListDataType)

        return adapter.fromJson(json)!!
    }

    fun getCartItemsAsJson(): String {
        return getListItemsAsJson(_cartItems.value!!)
    }

    fun getMachineItemsAsJson(): String {
        return getListItemsAsJson(machineItems.value!!)
    }

    fun getShelfItemsAsJson(): String {
        return getListItemsAsJson(shelfItems.value!!)
    }

    private fun getListItemsAsJson(items: List<Item>): String {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val itemListDataType: Type = Types.newParameterizedType(
            MutableList::class.java,
            Item::class.java
        )
        val adapter: JsonAdapter<List<Item>> = moshi.adapter(itemListDataType)

        return adapter.toJson(items)!!
    }
}