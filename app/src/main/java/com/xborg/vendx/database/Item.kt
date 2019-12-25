package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class ItemCategory {
    Snack,
    Beverage,
    FastFood,
    Other
}

data class Item (
    @Json(name = "Id")      var id: String,
    @Json(name = "Name")    var name: String,
    @Json(name = "Cost")    var cost: Long,
    @Json(name = "Image")   var imgScrUrl: String,
    @Json(name = "Category")var category: ItemCategory,

    var inShelf: Boolean = false,
    var inMachine: Boolean = false,
    var remainingInMachine: Long = 0,
    var remainingInShelf: Long = 0
)

data class ItemList (
    var items: List<Item>
)