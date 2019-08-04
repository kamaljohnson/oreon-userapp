package com.xborg.vendx

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginBtn = findViewById<TextView>(R.id.login_btn)
        loginBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        }
    }
}
