package com.xborg.vendx.database

data class ItemGroup (
    val title: String,
    val items : List<Item>,
    var draw_line_breaker: Boolean
)