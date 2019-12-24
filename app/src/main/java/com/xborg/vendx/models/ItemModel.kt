package com.xborg.vendx.models

import com.xborg.vendx.models.item.ItemCategory

data class ItemModel (
    var item_id:String = "",
    var name:String = "",
    var image_src: String = "",
    var category: ItemCategory = ItemCategory.OTHER,
    var cost:String = "",
    var quantity:String = "",
    var item_limit:String = "",
    var selectable: Boolean = true
)