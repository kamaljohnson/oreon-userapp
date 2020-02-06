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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

const val TAG = "Feedback"

class SharedViewModel : ViewModel() {

    val uid = FirebaseAuth.getInstance().uid.toString()
    var userFeedback =  MutableLiveData<Feedback>()

    var feedbackPosted = MutableLiveData<Boolean>()

    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun postFeedback() {
        val userFeedbackJson = Gson().toJson(userFeedback.value, Feedback::class.java)
        val feedbackCall = VendxApi.retrofitServices
            .postFeedbackAsync(id = uid, feedback = userFeedbackJson)
        feedbackCall.enqueue(object : Callback<Feedback> {
            override fun onResponse(call: Call<Feedback>, response: Response<Feedback>) {
                Log.i("Debug", "checkApplicationVersion")
                if(response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<Feedback>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
                if(error is SocketTimeoutException) {
                    //Connection Timeout
                    Log.e("Debug", "error type : connectionTimeout")
                } else if(error is IOException) {
                    //Timeout
                    Log.e("Debug", "error type : timeout")
                } else {
                    if(feedbackCall.isCanceled) {
                        //Call cancelled forcefully
                        Log.e("Debug", "error type : cancelledForcefully")
                    } else {
                        //generic error handling
                        Log.e("Debug", "error type : genericError")
                    }
                }
            }
        })
    }

}