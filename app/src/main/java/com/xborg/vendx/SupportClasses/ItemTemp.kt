package com.xborg.vendx.SupportClasses

import com.xborg.vendx.Models.ItemCategory

class Item {
    lateinit var category: ItemCategory
    lateinit var item_id:String
    lateinit var name:String
    lateinit var cost:String
    lateinit var quantity:String
    lateinit var item_limit:String
    lateinit var image_src: String
    var selectable: Boolean = true
}