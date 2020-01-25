package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Order (
     @SerializedName("Id")  var Id: String,
     @SerializedName("Cart")  var Cart: MutableMap<String, Int> = mutableMapOf(),
     @SerializedName("BillingCart")  var BillingCart: MutableMap<String, Int> = mutableMapOf(),
     @SerializedName("Amount")  var Amount: Float = 0f,
     @SerializedName("Uid")  var Uid: String = "",
     @SerializedName("TimeStamp")  var TimeStamp: String = "",
     @SerializedName("PaymentId")  var PaymentId: String = ""
)