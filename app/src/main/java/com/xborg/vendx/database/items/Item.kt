package com.xborg.vendx.database.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "item_table")
data class Item (
    @PrimaryKey
    var id: String,
    var name: String,
    var cost: Number,
    var inShelf: Boolean,
    var remainingInMachine: Number,
    var remainingInShelf: Number
)