package com.xborg.vendx.database


data class HomeInventoryGroups(
    var Title: String,
    var Inventory: List<InventoryItem> = listOf(),
    var Message: String = "",
    var PaidInventory: Boolean
)