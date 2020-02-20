package com.xborg.vendx.activities.loginActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


val db = FirebaseFirestore.getInstance()
private var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var callbackManager: CallbackManager
    private final var facebookEmail:String = "email"
    private var facebookAccessToken = AccessToken.getCurrentAccessToken()

    var isLoggedIn = facebookAccessToken != null && !facebookAccessToken!!.isExpired

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Facebook Login
        facebook_login_button.setOnClickListener {
            Log.i(TAG, "facebook login button clicked")

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.d("MainActivity", "Facebook onError.")

                    }
                })
        }

        // Google Login
        google_login_button.setOnClickListener {

        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
