package com.xborg.vendx.database

import android.content.Context
import android.util.Log
import androidx.room.*
import com.google.gson.annotations.SerializedName

abstract class UserDatabase : RoomDatabase() {

    abstract val userDatabaseDao: UserDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
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
interface UserDao {
    @Insert
    fun insert(user: User) {
        Log.i("Database", "user inserted to database: $user")
    }

    //TODO: use the user id in sharedPreference
    @Query("SELECT * from user_table WHERE id = :id")
    fun get(id: Long): ItemDetail?

    @Query("DELETE FROM user_table")
    fun clear()
}

@Entity(tableName = "user_table")
data class User(
    @SerializedName("id") var Id: String = "",
    @SerializedName("name") var Name: String = "",
    @SerializedName("email") var Email: String = "",
    @SerializedName("phone") var Phone: String = "",
    @SerializedName("location") var Location: Location,
    @SerializedName("inventory") var Inventory: List<InventoryItem> = ArrayList(),
    @SerializedName("cart") var Cart: List<CartItem> = ArrayList()
)