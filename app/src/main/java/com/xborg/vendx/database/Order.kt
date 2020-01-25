package com.xborg.vendx.database

data class Order (
     var id: String,
    var cart: MutableMap<String, Int> = mutableMapOf(),
    var billingCart: MutableMap<String, Int> = mutableMapOf(),
    var amount: Float = 0f,
    var uid: String = "",
    var timeStamp: String = "",
    var paymentId: String = ""
)