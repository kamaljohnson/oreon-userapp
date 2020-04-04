package com.xborg.vendx.database


data class HomeItemGroup(
    val Title: String,
    val Inventory: List<InventoryItem> = listOf(),
    var Message: String = "",
    var PaidInventory: Boolean
)