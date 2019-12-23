package com.xborg.vendx.Models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities= [Item::class], version = 1)
abstract class ItemDatabase: RoomDatabase(){
    companion object {
        public lateinit var instance: ItemDatabase

        @Synchronized
        public fun getInstance(context: Context): ItemDatabase {
            if(instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                    ItemDatabase::class as Class<ItemDatabase>, "item_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }

    public abstract var itemDao: ItemDao

}