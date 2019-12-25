package com.xborg.vendx.models

import com.xborg.vendx.database.Item
import com.xborg.vendx.database.ItemList

data class ItemGroupModel (
    val items : List<Item>,
    var draw_line_breaker: Boolean
)