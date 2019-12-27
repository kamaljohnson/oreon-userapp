package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class ItemCategory {
    Snack,
    Beverage,
    FastFood,
    Other
}

data class Item (
    @Json(name = "Id")                          var id: String,
    @Json(name = "Name")                        var name: String,
    @Json(name = "Cost")                        var cost: Long,
    @Json(name = "Image")                       var imgScrUrl: String,
    @Json(name = "Category")                    var category: ItemCategory,

    @Json(name = "InShelf")                     var inShelf: Boolean = false,
    @Json(name = "InMachine")                   var inMachine: Boolean = false,
    @Json(name = "RemainingInMachine")          var remainingInMachine: Long = 0,
    @Json(name = "RemainingInShelf")            var remainingInShelf: Long = 0
)

data class ItemList (
    var items: List<Item>
)