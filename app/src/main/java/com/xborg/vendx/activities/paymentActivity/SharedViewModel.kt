package com.xborg.vendx.activities.paymentActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.*
import java.io.Serializable

class SharedViewModel : ViewModel() {

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
//        order.value!!.Uid = uid
        order.value!!.Cart = cart
        order.value!!.BillingCart = billingCart

        paymentState.value = PaymentState.OrderInit
    }

    fun setCartItemsFromSerializableHashMap(cartItemsAsHash: Serializable) {

        val tempCartMap = cartItemsAsHash as MutableMap<String, Int>
//        val tempCartList = arrayListOf<Item>()

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

            }
        }

//        _cartItems.value = tempCartList
        initOrder(tempOrderCartMap, tempOrderBillingCartMap)
    }

    fun setMachineItemsFromSerializable(machineItemsAsJson: Serializable) {
//        machineItems.value = convertJsonToItemList(machineItemsAsJson as String)
    }

    fun setInventoryItemsFromSerializable(inventoryItemsAsJson: Serializable) {
//        inventoryItems.value = convertJsonToItemList(inventoryItemsAsJson as String)
    }

}