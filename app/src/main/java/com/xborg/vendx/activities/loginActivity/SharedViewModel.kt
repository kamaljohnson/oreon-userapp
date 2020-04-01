package com.xborg.vendx.activities.loginActivity

import android.util.Log
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.data.model.User
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedViewModel : ViewModel() {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)


    fun sendGoogleTokenId(token: String) {
        val userCall = VendxApi.retrofitServices.sendGoogleIdToken(token)
        userCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    Log.i("Debug", "Successful Response code : 200")
                    val user = response.body()
                    if (user != null) {
                        ioScope.launch {
                            //TODO: add user data to user-database
                        }
                    } else {
                        Log.e("Debug", "user received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<String>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    fun sendFacebookTokenId(token: String) {
        val userCall = VendxApi.retrofitServices.sendFacebookIdToken(token)
        userCall.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.i("Debug", "Successful Response code : 200")
                    val user = response.body()
                    if (user != null) {
                        ioScope.launch {
                            //TODO: add user data to user-database
                        }
                    } else {
                        Log.e("Debug", "user received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response")
                }
            }

            override fun onFailure(call: Call<String>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }
}