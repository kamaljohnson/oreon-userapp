package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class ItemGroup (
    @SerializedName("Title")  val Title: String,
    @SerializedName("items")  val items : List<Item> = listOf(),
    @SerializedName("DrawLineBreaker")  var DrawLineBreaker: Boolean,
    @SerializedName("ShowNoMachinesNearbyMessage")  var ShowNoMachinesNearbyMessage: Boolean = false
)