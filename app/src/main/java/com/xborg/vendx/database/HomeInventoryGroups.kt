package com.xborg.vendx.database


data class HomeInventoryGroups(
    val Title: String,
    val Inventory: List<InventoryItem> = listOf(),
    var Message: String = "",
    var PaidInventory: Boolean
)