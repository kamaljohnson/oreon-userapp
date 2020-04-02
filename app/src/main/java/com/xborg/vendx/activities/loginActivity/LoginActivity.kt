package com.xborg.vendx.activities.loginActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.xborg.vendx.R
import com.xborg.vendx.activities.loginActivity.fragments.EmailLoginFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private var ioScope = CoroutineScope(Dispatchers.IO)

    //Facebook
    private var facebookCallbackManager: CallbackManager = CallbackManager.Factory.create();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val application = requireNotNull(this).application
        val viewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        //        region Check Cache

        ioScope.launch {
            if(sharedViewModel.isAccessTokenPresentInCache()) {
                Log.i("Debug", "there is a cached access token")

                //TODO: use the cached accessToken to refresh and authorize the session
            } else {
                Log.i("Debug", "there is no cached access token")
                showLogin()
            }
        }

        //        endregion
    }

    private fun showLogin() {
        //        region Facebook Login

        facebook_login_button.setReadPermissions(listOf("email"))

        // Callback registration
        facebook_login_button.registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.i(TAG, "Access Token : " + loginResult!!.accessToken.token)
                sharedViewModel.sendFacebookTokenId(loginResult.accessToken.token)
            }

            override fun onCancel() {
                Log.i(TAG, "Login cancelled")
            }

            override fun onError(exception: FacebookException) {
                Log.i(TAG, "Login Error: $exception")
            }
        })

        facebookCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(facebookCallbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.i(TAG, "Access Token : " + loginResult!!.accessToken.token)
                    sharedViewModel.sendFacebookTokenId(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    Log.i(TAG, "Login cancelled")
                }

                override fun onError(exception: FacebookException) {
                    Log.i(TAG, "Login Error: $exception")
                }
            })

        //        endregion
        //        region Email Login

        email_login_button.setOnClickListener {
            Log.i(TAG, "email login button clicked")

            loadEmailLoginFragment()
        }

        //        endregion
    }

    @SuppressLint("ResourceType")
    private fun loadEmailLoginFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.email_login_container,
            EmailLoginFragment(),
            "EmailLoginFragment"
        )

        fragmentTransaction.commit()
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
