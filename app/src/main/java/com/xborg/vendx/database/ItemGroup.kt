package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class ItemGroup (
    @SerializedName("Title")  val Title: String,
    @SerializedName("Items")  val Items : List<Item> = listOf(),
    @SerializedName("DrawLineBreaker")  var DrawLineBreaker: Boolean,
    @SerializedName("ShowNoMachinesNearbyMessage")  var ShowNoMachinesNearbyMessage: Boolean = false
)