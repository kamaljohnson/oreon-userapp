package com.xborg.vendx.database

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemGroup (
    val title: String,
    val items : List<Item> = listOf(),
    var drawLineBreaker: Boolean,
    var showNoMachinesNearbyMessage: Boolean = false
)