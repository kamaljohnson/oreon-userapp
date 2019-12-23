package com.xborg.vendx.Models
import androidx.room.Entity
import androidx.room.PrimaryKey


enum class ItemCategory {
    SNACK,
    BEVERAGE,
    FAST_FOOD,
    STATIONARY,
    OTHER
}

@Entity(tableName = "item_table")
public class Item (
    @field:PrimaryKey
    private val id: String,
    private val name: String,
    private val cost: Number,
    private val image_scr: String,
    private val category: ItemCategory,
    // region Shelf properties
    private val from_shelf: Boolean = false,
    private val remaining: Number,
    // endregion
    // region Machine properties
    private val in_machine: Boolean = false
    // endregion
){
    // region Getter methods
    public fun getId(): String {
        return id
    }
    public fun getName(): String {
        return name
    }
    public fun getCost(): Number {
        return cost
    }
    public fun getImageScr(): String {
        return image_scr
    }
    public fun getCategory(): ItemCategory {
        return category
    }
    public fun isFromShelf(): Boolean {
        return from_shelf
    }
    public fun geItemsRemaining(): Number {
        return remaining
    }
    public fun isInMachine(): Boolean {
        return in_machine
    }
    // endregion
}
