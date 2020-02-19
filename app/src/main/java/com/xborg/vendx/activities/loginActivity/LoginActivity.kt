package com.xborg.vendx.activities.loginActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.R

val db = FirebaseFirestore.getInstance()
private var TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
