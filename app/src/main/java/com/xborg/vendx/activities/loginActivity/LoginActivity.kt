package com.xborg.vendx.activities.loginActivity

//import com.xborg.vendx.activities.mainActivity.MainActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


private var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    //Facebook Login
    var callbackManager: CallbackManager = CallbackManager.Factory.create();

    val EMAIL = "email"

    //Google Login
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGNIN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

//        region Facebook Login

        login_button.setReadPermissions(Arrays.asList(EMAIL))

        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
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
//        region Google Login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("510464295618-1sl81vuuellu04ef8ki11qlusp8m2p78.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_login_button.setOnClickListener {
            Log.i(TAG, "google login button clicked")
            googleSignIn()
        }
//        endregion
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, GOOGLE_SIGNIN
        )
    }

    private fun googleSignOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }

//This can be used when you have the ‘Delete my account’ option in the user’s profile)
    private fun revokeGoogleAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGNIN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i(TAG, "Google ID: $googleId")

            val googleFirstName = account?.givenName ?: ""
            Log.i(TAG, "Google First Name: $googleFirstName")

            val googleLastName = account?.familyName ?: ""
            Log.i(TAG, "Google Last Name: $googleLastName")

            val googleEmail = account?.email ?: ""
            Log.i(TAG, "Google Email: $googleEmail")

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i(TAG, "Google Profile Pic URL: $googleProfilePicURL")

            val googleIdToken = account?.idToken ?: ""
            Log.i(TAG, "Google Id Token: $googleIdToken")

            sharedViewModel.sendGoogleTokenId(googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.i(TAG, "Error: $e")
        }
    }
}
