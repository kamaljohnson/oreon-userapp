package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("name") var Name: String
) {
    override fun toString(): String {
        return "category='$Name'"
    }
}


data class ItemDetail(
    @SerializedName("id") var Id: String,
    @SerializedName("name") var Name: String,
    @SerializedName("cost") var Cost: Long,
    @SerializedName("discounted_cost") var DiscountedCost: Long,
    @SerializedName("category") var Category: Category,
    @SerializedName("foreground_asset") var ForegroundAsset: String,
    @SerializedName("background_asset") var BackgroundAsset: String,
    @SerializedName("content_asset") var ContentAsset: String
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', cost='$Cost', discounted_cost='$DiscountedCost', category='$Category')"
    }
}