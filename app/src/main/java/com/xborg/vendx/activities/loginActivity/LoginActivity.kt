package com.xborg.vendx.activities.loginActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

val db = FirebaseFirestore.getInstance()
private var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private var user = FirebaseAuth.getInstance().currentUser
    private var emailUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailUploaded = user?.email != null

        Log.i(TAG, "email Uploaded: $emailUploaded")

        if(user != null && emailUploaded) {
            loadMainActivity()
        } else {
            createSignInIntent()
        }

        done_button.setOnClickListener {
            user = FirebaseAuth.getInstance().currentUser
            //check if valid email id
            Log.e(TAG, "done button clicked")
            if(email_id.text.toString() != "") {
                user?.updateEmail(email_id.text.toString())
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "email id updated", Toast.LENGTH_SHORT).show()
                            emailUploaded = true
                            loadMainActivity()
                        } else {
                            Log.e(TAG, "email upload error : " + task.exception)
                            Toast.makeText(this, "email uploading failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                Toast.makeText(this, "email field must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                /*TODO: check
                 * if user already present in database
                 *  -
                 * else
                 *  - create user with id = uid
                 *  - init inventory {}
                */
                val uid = FirebaseAuth.getInstance().uid.toString()
                val usersRef = db.collection("Users").document(uid)
                usersRef.get()
                    .addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val user = HashMap<String, Any>()
                            val emptyMap = HashMap<String, Number>()
                            user["Inventory"] = emptyMap
                            db.collection("Users").document(uid)
                                .set(user)
                                .addOnSuccessListener { userRef ->
                                    Log.d(TAG, "---> UserReference created with ID: $uid")
                                    loadMainActivity()
                                }
                                .addOnFailureListener{
                                    Log.d(TAG, "Failed to create the user")
                                    Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Log.e(TAG, "--> already present in database")
                            loadMainActivity()
                        }
                    }
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMainActivity() {
        if(emailUploaded) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            user_email_request_layout.visibility = View.VISIBLE
        }
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
            }
    }

    private fun delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
            }
    }

    private fun themeAndLogo() {
        val providers = emptyList<AuthUI.IdpConfig>()
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.fui_idp_button_background_google) // Set logo drawable
                .setTheme(R.style.AppTheme) // Set theme
                .build(),
            RC_SIGN_IN
        )
    }

    private fun privacyAndTerms() {
        val providers = emptyList<AuthUI.IdpConfig>()
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls(
                    "https://example.com/terms.html",
                    "https://example.com/privacy.html")
                .build(),
            RC_SIGN_IN
        )
    }

    companion object {

        private const val RC_SIGN_IN = 123
    }
}
