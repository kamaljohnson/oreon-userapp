package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

enum class Category {
    @SerializedName("Snack")  Snack,
    @SerializedName("Beverage")  Beverage,
    @SerializedName("FastFood")  FastFood,
    @SerializedName("Other")  Other
}

data class Item(
    @SerializedName("Id")  var Id: String,
    @SerializedName("Name")  var Name: String,
    @SerializedName("Cost")  var Cost: Long,
    @SerializedName("Package")  var PackageImageUrl: String,
    @SerializedName("Info")  var InfoImageUrl: String,
    @SerializedName("Bg")  var BgImageUrl: String,
    @SerializedName("Category")  var Category: Category,

    @SerializedName("InInventory")  var InInventory: Boolean = false,
    @SerializedName("InMachine")  var InMachine: Boolean = false,
    @SerializedName("RemainingInMachine")  var RemainingInMachine: Int = -1,
    @SerializedName("RemainingInInventory")  var RemainingInInventory: Int = -1,

    @SerializedName("CartCount")  var cartCount: Int = 0
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', inShelf=$InInventory, inMachine=$InMachine, remainingInMachine=$RemainingInMachine, remainingInShelf=$RemainingInInventory)"
    }
}