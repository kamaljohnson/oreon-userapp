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
    @SerializedName("Image")  var ImgScrUrl: String,
    @SerializedName("Category")  var Category: Category,

    @SerializedName("InShelf")  var InShelf: Boolean = false,
    @SerializedName("InMachine")  var InMachine: Boolean = false,
    @SerializedName("RemainingInMachine")  var RemainingInMachine: Int = -1,
    @SerializedName("RemainingInShelf")  var RemainingInShelf: Int = -1,

    @SerializedName("CartCount")  var cartCount: Int = 0
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', inShelf=$InShelf, inMachine=$InMachine, remainingInMachine=$RemainingInMachine, remainingInShelf=$RemainingInShelf)"
    }
}