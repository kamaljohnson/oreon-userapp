package com.xborg.vendx.Models
import com.xborg.vendx.SupportClasses.Item

data class ItemGroupModel (
    val items : ArrayList<ItemModel>,
    var draw_line_breaker: Boolean
)