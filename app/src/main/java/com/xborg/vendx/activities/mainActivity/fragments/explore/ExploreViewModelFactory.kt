package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ExploreViewModelFactory (
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}