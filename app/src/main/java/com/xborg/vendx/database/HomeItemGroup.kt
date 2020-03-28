package com.xborg.vendx.database

import com.xborg.vendx.R


data class HomeItemGroup(
    val Title: String,
    val Items: List<ItemDetail> = listOf(),
    var Message: String = R.string.empty_msg.toString()
)