package com.xborg.vendx.database


data class HomeItemGroup(
    val Title: String,
    val Items: List<ItemCard> = listOf(),
    var Message: String = ""
)