package com.xborg.vendx.database.machine

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.xborg.vendx.database.InventoryItem
import com.xborg.vendx.database.Location
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
}


@Database(entities = [Machine::class], version = 2)
@TypeConverters(Converters::class)
abstract class MachineDatabase : RoomDatabase() {

    abstract fun machineDao(): MachineDao

    companion object {

        @Volatile
        private var instence: MachineDatabase? = null

        fun getInstance(context: Context): MachineDatabase {
            return instence
                ?: synchronized(this) {
                instence
                    ?: buildDatabase(
                        context
                    )
                        .also { instence = it }
            }
        }

        private fun buildDatabase(context: Context): MachineDatabase {
            return Room.databaseBuilder(
                context, MachineDatabase::class.java,
                "machine"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

@Dao
interface MachineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(machine: Machine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(machines: List<Machine>)

    @Query("SELECT * FROM machines_table")
    fun get(): LiveData<List<Machine>>

    @Query("SELECT * from machines_table WHERE name = :name")
    fun get(name: String): Machine?

    @Query("DELETE FROM machines_table")
    fun clear()
}

@Entity(tableName = "machines_table")
data class Machine(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id") var Id: Long?,

    @ColumnInfo(name = "name")
    @SerializedName("name") var Name: String?,

    @ColumnInfo(name = "mac")
    @SerializedName("mac") var Mac: String?,

//    @ColumnInfo(name = "location")
//    @SerializedName("location") var Location: Location?,

    @ColumnInfo(name = "distance")
    @SerializedName("distance") var Distance: Double?,

    @ColumnInfo(name = "inventory")
    @SerializedName("inventory") var Inventory: List<InventoryItem> = ArrayList()
){
    @Ignore
    constructor() :
            this (null, null, null, null)
}
