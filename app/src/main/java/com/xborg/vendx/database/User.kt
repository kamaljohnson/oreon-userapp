package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") var Id: String = "",
    @SerializedName("name") var Name: String = "",
    @SerializedName("email") var Email: String = "",
    @SerializedName("phone") var Phone: String = "",
    @SerializedName("location") var Location: Location,
    @SerializedName("inventory") var Inventory: List<InventoryItem> = ArrayList(),
    @SerializedName("cart") var Cart: List<CartItem> = ArrayList()
)

data class CartItem(
    @SerializedName("paid") var Paid: Boolean,
    @SerializedName("item_detail") var ItemDetailId: String,
    @SerializedName("quantity") var Quantity: Number
)