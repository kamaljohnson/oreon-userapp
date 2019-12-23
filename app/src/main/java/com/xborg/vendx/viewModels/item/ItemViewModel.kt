package com.xborg.vendx.viewModels.item

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.xborg.vendx.models.item.Item
import com.xborg.vendx.repositories.ItemRepository

public class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private var repository = ItemRepository(application)
    private var allItems: LiveData<List<Item>>

    init {
        allItems = repository.getAllItems()
    }

    public fun insert(item: Item) {
        repository.insert(item)
    }
    public fun update(item: Item) {
        repository.update(item)
    }
    public fun delete(item: Item) {
        repository.delete(item)
    }
    public fun deleteAllItems() {
        repository.deleteAllItems()
    }
    public fun getAllItems() : LiveData<List<Item>> {
        return allItems
    }

}