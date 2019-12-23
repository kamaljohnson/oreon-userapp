package com.xborg.vendx.models.item

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xborg.vendx.models.item.Item

@Dao
interface ItemDao {

    @Insert
    fun insert(item: Item)

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)

    @Query("DELETE FROM item_table")
    fun deleteAllItems()

    @Query("SELECT * FROM item_table ORDER BY category DESC")
    fun getAllItems(): LiveData<List<Item>>
}