package com.xborg.vendx.activities.mainActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xborg.vendx.database.ItemDetailDao
import java.lang.IllegalArgumentException

class SharedViewModelFactory (
    private val dataSource: ItemDetailDao,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}