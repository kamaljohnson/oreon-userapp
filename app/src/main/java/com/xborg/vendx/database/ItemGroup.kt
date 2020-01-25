package com.xborg.vendx.database

data class ItemGroup (
    val title: String,
    val items : List<Item> = listOf(),
    var drawLineBreaker: Boolean,
    var showNoMachinesNearbyMessage: Boolean = false
)