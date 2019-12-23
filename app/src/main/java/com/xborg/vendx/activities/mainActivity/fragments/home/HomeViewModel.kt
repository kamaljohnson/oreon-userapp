package com.xborg.vendx.activities.mainActivity.fragments.home

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {
    init {
        Log.i(TAG, "HomeViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "HomeViewModel destroyed!")
    }
}