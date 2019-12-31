package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Order (
    @Json(name = "Id") var id: String,
    @Json(name = "Cart") var cart: MutableMap<String, Int> = mutableMapOf(),
    @Json(name = "BillingCart") var billingCart: MutableMap<String, Int> = mutableMapOf(),
    @Json(name = "Amount") var amount: Float = 0f,
    @Json(name = "UID") var uid: String = "",
    @Json(name = "PaymentId") var paymentId: String = ""
)