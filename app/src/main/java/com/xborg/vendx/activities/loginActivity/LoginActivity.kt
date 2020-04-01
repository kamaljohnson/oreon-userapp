package com.xborg.vendx.activities.loginActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.xborg.vendx.R
//import com.xborg.vendx.activities.mainActivity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*


private var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var callbackManager: CallbackManager
    private final var facebookEmail:String = "email"
    private var facebookAccessToken = AccessToken.getCurrentAccessToken()

    var isLoggedIn = facebookAccessToken != null && !facebookAccessToken!!.isExpired

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val GOOGLE_SIGNIN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

//        region Facebook Login
        facebook_login_button.setOnClickListener {
            Log.i(TAG, "facebook login button clicked")

            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)
//                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.d("MainActivity", "Facebook onError.")

                    }
                })
        }
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
//        callbackManager.onActivityResult(requestCode, resultCode, data)
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
