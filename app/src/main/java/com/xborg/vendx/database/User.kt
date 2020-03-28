package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name") var Name: String ="",
    @SerializedName("email") var Email: String = "",
    @SerializedName("phone") var Phone: String = "",
    @SerializedName("location") var Location: Location = Location(0.0, 0.0),
    @SerializedName("inventory") var Inventory: List<UserInventoryItem> = ArrayList()
)

data class UserInventoryItem(
    @SerializedName("item_detail") var ItemDetailId: String,
    @SerializedName("discounted_cost") var DiscountedCost: Float,
    @SerializedName("quantity") var Quantity: Number
)