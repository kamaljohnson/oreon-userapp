package com.xborg.vendx.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun stringToLocation(value: String?): Location? {
        val locationType: Type = object : TypeToken<Location>() {}.type
        return Gson().fromJson(value, locationType)
    }

    @TypeConverter
    fun locationToString(list: Location?): String? {
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

@Database(entities = [User::class], version = 4)
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * from user_table LIMIT 1")
    fun get(): LiveData<User?>?

    @Query("DELETE FROM user_table")
    fun clear()
}

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id") var Id: String,

    @ColumnInfo(name = "first_name")
    @SerializedName("first_name") var FirstName: String?,

    @ColumnInfo(name = "last_name")
    @SerializedName("last_name") var LastName: String?,

    @ColumnInfo(name = "email")
    @SerializedName("email") var Email: String,

    @ColumnInfo(name = "phone")
    @SerializedName("phone") var Phone: String?,

    @ColumnInfo(name = "profile_pic")
    @SerializedName("profile_pic") var ProfilePic: String?,

    @ColumnInfo(name = "location")
    @SerializedName("location") var Location: Location?,

    @ColumnInfo(name = "inventory")
    @SerializedName("inventory") var Inventory: List<InventoryItem> = ArrayList()
)