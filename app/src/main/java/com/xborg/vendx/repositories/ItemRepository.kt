package com.xborg.vendx.repositories

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.xborg.vendx.models.item.Item
import com.xborg.vendx.models.item.ItemDao
import com.xborg.vendx.models.item.ItemDatabase

public class ItemRepository {
    private lateinit var itemDao: ItemDao
    private lateinit var allItems: LiveData<List<Item>>

    public fun ItemRepository(application: Application) {
        var database: ItemDatabase = ItemDatabase.getInstance(application)
        itemDao = database.itemDao
        allItems = itemDao.getAllItems()
    }

    public fun insert(item: Item) {
        InsertItemAsyncTask(itemDao).execute(item)
    }
    public fun update(item: Item) {
        UpdateItemAsyncTask(itemDao).execute(item)
    }
    public fun delete(item: Item) {
        DeleteItemAsyncTask(itemDao).execute(item)
    }
    public fun deleteAllItems() {
        DeleteAllItemsAsyncTask(itemDao).execute()
    }
    public fun getAllItems() : LiveData<List<Item>> {
        return allItems
    }

    companion object {
        private class InsertItemAsyncTask(itemDao: ItemDao): AsyncTask<Item, Void, Void>() {
            private  var itemDao: ItemDao = itemDao

            override fun doInBackground(vararg item: Item): Void? {
                itemDao.insert(item[0])
                return null
            }
        }
        private class UpdateItemAsyncTask(itemDao: ItemDao): AsyncTask<Item, Void, Void>() {
            private  var itemDao: ItemDao = itemDao

            override fun doInBackground(vararg item: Item): Void? {
                itemDao.update(item[0])
                return null
            }
        }
        private class DeleteItemAsyncTask(itemDao: ItemDao): AsyncTask<Item, Void, Void>() {
            private  var itemDao: ItemDao = itemDao

            override fun doInBackground(vararg item: Item): Void? {
                itemDao.delete(item[0])
                return null
            }
        }
        private class DeleteAllItemsAsyncTask(itemDao: ItemDao): AsyncTask<Void, Void, Void>() {
            private  var itemDao: ItemDao = itemDao

            override fun doInBackground(vararg voids: Void): Void? {
                itemDao.deleteAllItems()
                return null
            }
        }
    }

}