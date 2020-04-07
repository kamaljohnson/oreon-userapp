package com.xborg.vendx.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeViewModel
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeViewModel.Companion.context

// region ItemDetail
@Database(entities = [ItemDetail::class], version = 1)
abstract class ItemDetailDatabase : RoomDatabase() {

    abstract val itemDetailDatabaseDao: ItemDetailDao

    companion object {

        @Volatile
        private var INSTANCE: ItemDetailDatabase? = null

        fun getInstance(context: Context): ItemDetailDatabase  {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ItemDetailDatabase::class.java,
                        "item_detail_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

@Dao
interface ItemDetailDao {
    @Insert
    fun insert(itemDetail: ItemDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(itemDetails: List<ItemDetail>)

    @Query("SELECT * FROM item_details_table")
    fun get(): LiveData<List<ItemDetail>>

    @Query("SELECT * from item_details_table WHERE id = :id")
    fun get(id: String): ItemDetail?

    @Query("DELETE FROM item_details_table")
    fun clear()
}

@Entity(tableName = "item_details_table")
data class ItemDetail(
    @PrimaryKey
    @SerializedName("id") var Id: String,

    @ColumnInfo(name = "name")
    @SerializedName("name") var Name: String,

    @ColumnInfo(name = "cost")
    @SerializedName("cost") var Cost: Long,

    @ColumnInfo(name = "discounted_cost")
    @SerializedName("discounted_cost") var DiscountedCost: Long,

    @ColumnInfo(name = "category")
    @SerializedName("category") var Category: String,

    @ColumnInfo(name = "foreground_asset")
    @SerializedName("foreground_asset") var ForegroundAsset: String,

    @ColumnInfo(name = "background_asset")
    @SerializedName("background_asset") var BackgroundAsset: String,

    @ColumnInfo(name = "content_asset")
    @SerializedName("content_asset") var ContentAsset: String
) {
    override fun toString(): String {
        return "Item(id='$Id', name='$Name', cost='$Cost', discounted_cost='$DiscountedCost', category='$Category')"
    }
}

// endregion

data class InventoryItem(
    @SerializedName("item_detail") var ItemDetailId: String,
    @SerializedName("quantity") var Quantity: Int
)


@Database(entities = [CartItem::class], version = 1)
abstract class CartItemDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    companion object {

        @Volatile
        private var instence: CartItemDatabase? = null

        fun getInstance(context: Context): CartItemDatabase {
            return instence ?: synchronized(this) {
                instence ?: buildDatabase(context).also { instence = it }
            }
        }

        private fun buildDatabase(context: Context): CartItemDatabase {
            return Room.databaseBuilder(
                context, CartItemDatabase::class.java,
                "cart_item"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

enum class CartStatusCode {

    ItemNotInMachine,
    MachineNotSelected,
    ItemNotRemainingInMachine,
    ItemAddedSuccessfully,

}

@Dao
abstract class CartItemDao {

    fun addItem(itemId: String, paid: Boolean): CartStatusCode {

        val previousItem = get(itemId, paid)

        if(previousItem != null) {

            var count = previousItem.Count

            if(previousItem.Remaining!! > 1) {

                previousItem.Count += 1

                update(previousItem)

                return CartStatusCode.ItemAddedSuccessfully
            }

            return CartStatusCode.ItemNotRemainingInMachine

        } else {

            var remaining = 0

            HomeViewModel.selectedMachine.value!!.Inventory.forEach { item ->

                if(item.ItemDetailId == itemId) {

                    remaining = item.Quantity

                    val newCartItem: CartItem = CartItem(itemDetailId = itemId, paid = paid, remaining = remaining)

                    insert(newCartItem)

                    return CartStatusCode.ItemAddedSuccessfully
                }
            }

            return CartStatusCode.ItemNotInMachine
        }
    }

    fun removeItem(itemId: String, paid: Boolean) {

        val previousItem = get(itemId, paid)

        var count = previousItem!!.Count

        if (count > 1) {

            count -= 1

            previousItem.Count = count

            update(previousItem)
        } else {

            delete(previousItem)
        }
    }

    fun reset() {
        clear()
        //TODO: reset the auto_increment id to 0
    }

    fun processCart(itemId: String, limit: Int) {

        val oldCartItemPaid = get(itemId, true)
        val oldCartItem = get(itemId, false)

        val oldRemaining = oldCartItem?.Remaining ?: oldCartItemPaid?.Remaining ?: 0

        var updatedRemaining: Int = 0

        updatedRemaining = limit - ((oldCartItem?.Count ?: 0) + (oldCartItemPaid?.Count ?: 0))

        if(oldRemaining == updatedRemaining) {
            return
        }

        if(oldCartItem != null) {

            oldCartItem.Remaining = updatedRemaining
            update(oldCartItem)

        }

        if(oldCartItemPaid != null) {

            oldCartItemPaid.Remaining = updatedRemaining
            update(oldCartItemPaid)

        }
    }

    @Query("SELECT * from cart_item_table")
    abstract fun getLiveCartItems(): LiveData<List<CartItem>>

    @Query("SELECT * from cart_item_table WHERE :itemId = item_detail_id AND :paid = paid")
    abstract fun getLiveCartItem(itemId: String, paid: Boolean): LiveData<CartItem>

    @Query("SELECT * from cart_item_table WHERE :itemId = item_detail_id AND :paid = paid")
    abstract fun get(itemId: String, paid: Boolean): CartItem?


    @Insert
    abstract fun insert(cartItem: CartItem)

    @Update
    abstract fun update(cartItem: CartItem)


    @Query("DELETE FROM cart_item_table")
    abstract fun clear()

    @Delete
    abstract fun delete(cartItem: CartItem)
}


@Entity(tableName = "cart_item_table")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val Id: Long?,

    @ColumnInfo(name = "item_detail_id")
    @SerializedName("item_detail") var ItemDetailId: String,

    @ColumnInfo(name = "paid")
    @SerializedName("paid") var Paid: Boolean,

    @ColumnInfo(name = "count")
    @SerializedName("count") var Count: Int,

    @ColumnInfo(name = "remaining")
    @SerializedName("remaining") var Remaining: Int?

) {
    @Ignore
    constructor(itemDetailId: String, paid: Boolean, count:Int = 1, remaining:Int? = 0) :
            this (null, itemDetailId, paid, count, remaining)
}

enum class CartContext {
    None,
    Machine,
    Shop
}