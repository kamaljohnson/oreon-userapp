package com.xborg.vendx.SupportClasses

enum class ItemCategory {
    SNACK,
    BEVERAGE,
    FAST_FOOD,
    STATIONARY,
    OTHER
}

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