package com.xborg.vendx.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.annotations.SerializedName

// region ItemDetail
@Database(entities = [ItemDetail::class], version = 1, exportSchema = false)
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

    @Insert
    fun insert(itemDetails: List<ItemDetail>)

    @Query("SELECT * FROM item_details_table")
    fun get(): LiveData<List<ItemDetail>>

    @Query("SELECT * from item_details_table WHERE id = :id")
    fun get(id: Long): ItemDetail?

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

data class ItemCard(
    var ItemDetail: ItemDetail,
    var PurchaseLimit: Number,
    var Quantity: Number,
    var Paid: Boolean
)

data class InventoryItem(
    @SerializedName("item_detail") var ItemDetailId: String,
    @SerializedName("quantity") var Quantity: Number
)

data class CartItem(

    @ColumnInfo(name = "paid")
    @SerializedName("paid") var Paid: Boolean,

    @ColumnInfo(name = "item_detail_id")
    @SerializedName("item_detail") var ItemDetailId: String,

    @ColumnInfo(name = "quantity")
    @SerializedName("quantity") var Quantity: Number
)