package com.xborg.vendx.database

import android.content.Context
import android.util.Log
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun stringToLocation(value: String?): Location {
        val locationType: Type = object : TypeToken<Location>() {}.type
        return Gson().fromJson(value, locationType)
    }

    @TypeConverter
    fun locationToString(list: Location): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToInventoryList(value: String?): List<InventoryItem>? {
        val listType =
            object : TypeToken<List<InventoryItem?>?>() {}.type
        return Gson().fromJson<List<InventoryItem>>(value, listType)
    }

    @TypeConverter
    fun inventoryListToString(list: List<InventoryItem?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToCart(value: String?): List<CartItem>? {
        val listType =
            object : TypeToken<List<CartItem?>?>() {}.type
        return Gson().fromJson<List<CartItem>>(value, listType)
    }

    @TypeConverter
    fun cartToString(list: List<CartItem?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var instence: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return instence ?: synchronized(this) {
                instence ?: buildDatabase(context).also { instence = it }
            }
        }

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(
                context, UserDatabase::class.java,
                "user"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    //TODO: use the user id in sharedPreference
    @Query("SELECT * from user_table WHERE id = :id")
    fun get(id: Long): User?

    @Query("DELETE FROM user_table")
    fun clear()
}

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id") var Id: String,

    @ColumnInfo(name = "name")
    @SerializedName("name") var Name: String,

    @ColumnInfo(name = "email")
    @SerializedName("email") var Email: String,

    @ColumnInfo(name = "phone")
    @SerializedName("phone") var Phone: String,

    @ColumnInfo(name = "location")
    @SerializedName("location") var Location: Location,

    @ColumnInfo(name = "inventory")
    @SerializedName("inventory") var Inventory: List<InventoryItem> = ArrayList(),

    @ColumnInfo(name = "cart")
    @SerializedName("cart") var Cart: List<CartItem> = ArrayList()
)