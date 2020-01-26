package com.xborg.vendx.activities.feedbackActivity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.xborg.vendx.database.Feedback
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val TAG = "Feedback"

class SharedViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()
    var userFeedback =  MutableLiveData<Feedback>()

    var feedbackPosted = MutableLiveData<Boolean>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun postFeedback() {
        val userFeedbackJson = Gson().toJson(userFeedback.value, Feedback::class.java)
        coroutineScope.launch {
            val createOrderDeferred = VendxApi.retrofitServices
                .postFeedbackAsync(id = uid, feedback = userFeedbackJson)
            try {
                val listResult = createOrderDeferred.await()
                Log.i(TAG, "Successful to get response: $listResult")

            } catch (t: Throwable) {
                Log.e(TAG, "Failed to get response: ${t.message}")
            }
        }
    }

}