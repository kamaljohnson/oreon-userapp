package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class ItemDetail(
    @SerializedName("id") var Id: String,
    @SerializedName("name") var Name: String,
    @SerializedName("cost") var Cost: Long,
    @SerializedName("discounted_cost") var DiscountedCost: Long,
    @SerializedName("category") var Category: String,
    @SerializedName("foreground_asset") var ForegroundAsset: String,
    @SerializedName("background_asset") var BackgroundAsset: String,
    @SerializedName("content_asset") var ContentAsset: String
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', cost='$Cost', discounted_cost='$DiscountedCost', category='$Category')"
    }
}

data class ItemCard(
    var ItemDetail: ItemDetail,
    var PurchaseLimit: Number,
    var Quantity: Number,
    var Paid: Boolean
)

data class InventoryItem(
    @SerializedName("item_detail") var ItemDetailId: String,
    @SerializedName("quantity") var Quantity: Number
)
