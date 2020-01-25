package com.xborg.vendx.database


enum class ItemCategory {
    Snack,
    Beverage,
    FastFood,
    Other
}

data class Item(
    var id: String,
    var name: String,
    var cost: Long,
    var imgScrUrl: String,
    var category: ItemCategory,

    var inShelf: Boolean = false,
    var inMachine: Boolean = false,
    var remainingInMachine: Int = -1,
    var remainingInShelf: Int = -1,

    var cartCount: Int = 0
) {
    override fun toString(): String {
        return "Item(id='$id', name='$name', inShelf=$inShelf, inMachine=$inMachine, remainingInMachine=$remainingInMachine, remainingInShelf=$remainingInShelf)"
    }
}

data class ItemList(
    var items: List<Item>
)