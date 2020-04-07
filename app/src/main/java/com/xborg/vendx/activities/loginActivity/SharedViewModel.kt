package com.xborg.vendx.activities.loginActivity

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.database.AccessToken
import com.xborg.vendx.database.AccessTokenDatabase
import com.xborg.vendx.network.VendxApi
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val accessTokenDao = AccessTokenDatabase.getInstance(application).accessTokenDao()

    suspend fun isAccessTokenPresentInCache(): Boolean {
        return withContext(Dispatchers.IO) {
            val cachedAccessToken = accessTokenDao.get()
            cachedAccessToken != null
        }
    }

    suspend fun refreshAccessToken() {
        withContext(Dispatchers.IO) {
            val cachedAccessToken = accessTokenDao.get()
            val refreshToken = cachedAccessToken!!.refreshToken
            val token = cachedAccessToken.token
            if (token == null) {

                _refreshAccessToken(refreshToken!!)
            } else {
                loadMainActivity()
            }
        }
    }

    private fun _refreshAccessToken(refresh_token: String) {
        val accessTokenCall = VendxApi.retrofitServices.refreshAccessToken(refresh_token)
        accessTokenCall.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    Log.i("Debug", "Successful Response code : 200")
                    val accessToken = response.body()
                    Log.i("Debug", "Access Token : $accessToken")
                    if (accessToken != null) {
                        ioScope.launch {
                            accessTokenDao.insert(accessToken)
                            loadMainActivity()
                        }
                    } else {
                        Log.e("Debug", "user received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response : $response")
                }
            }

            override fun onFailure(call: Call<AccessToken>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    fun sendFacebookTokenId(token: String) {
        val accessTokenCall = VendxApi.retrofitServices.sendLoginIdToken(token, "facebook")
        accessTokenCall.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    Log.i("Debug", "Successful Response code : 200")
                    val accessToken = response.body()
                    Log.i("Debug", "Access Token : $accessToken")
                    if (accessToken != null) {
                        ioScope.launch {
                            accessTokenDao.insert(accessToken)
                            loadMainActivity()
                        }
                    } else {
                        Log.e("Debug", "user received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response : $response")
                }
            }

            override fun onFailure(call: Call<AccessToken>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    fun sendNameAndEmail(name: String, email: String) {
        val accessTokenCall = VendxApi.retrofitServices.sendLoginNameAndEmail(email)
        accessTokenCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.i("Debug", "Successful Response code : 200")
                } else {
                    Log.e("Debug", "Failed to get response : $response")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    fun sendEmailToken(email: String, token: String) {
        val accessTokenCall = VendxApi.retrofitServices.sendEmailToken(email, token)
        accessTokenCall.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    Log.i("Debug", "Successful Response code : 200")

                    val accessToken = response.body()
                    Log.i("Debug", "Access Token : $accessToken")
                    if (accessToken != null) {
                        ioScope.launch {
                            accessTokenDao.insert(accessToken)
                            loadMainActivity()
                        }
                    } else {
                        Log.e("Debug", "user received is null")
                    }
                } else {
                    Log.e("Debug", "Failed to get response : $response")
                }
            }

            override fun onFailure(call: Call<AccessToken>, error: Throwable) {
                Log.e("Debug", "Failed to get response ${error.message}")
            }
        })
    }

    @UiThread
    private fun loadMainActivity() {
        val intent = Intent(getApplication(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(getApplication(), intent, Bundle())
    }

}