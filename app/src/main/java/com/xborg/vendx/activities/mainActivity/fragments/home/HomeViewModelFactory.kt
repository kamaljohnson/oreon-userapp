package com.xborg.vendx.activities.mainActivity.fragments.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xborg.vendx.database.UserDao
import java.lang.IllegalArgumentException

class HomeViewModelFactory (
    private val dataSource: UserDao,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}