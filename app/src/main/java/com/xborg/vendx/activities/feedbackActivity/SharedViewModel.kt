package com.xborg.vendx.activities.feedbackActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xborg.vendx.database.Feedback

const val TAG = "Feedback"

class SharedViewModel : ViewModel() {

    var userFeedback = MutableLiveData<Feedback>()

    var feedbackPosted = MutableLiveData<Boolean>()

    fun postFeedback() {

    }

}