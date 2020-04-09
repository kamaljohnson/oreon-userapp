package com.xborg.vendx.activities.customerSupportActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xborg.vendx.R
import com.xborg.vendx.activities.customerSupportActivity.fragments.chat.ChatFragment
import com.xborg.vendx.activities.customerSupportActivity.fragments.home.HomeFragment

class CustomerSupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customer_support_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance())
                .commitNow()
        }
    }

    private fun goBack() {
        finish()
    }
}
