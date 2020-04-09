package com.xborg.vendx.activities.customerSupportActivity.fragments.chat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ChatViewModelFactory (
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}