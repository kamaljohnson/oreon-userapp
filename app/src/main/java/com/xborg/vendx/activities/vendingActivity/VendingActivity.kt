package com.xborg.vendx.activities.vendingActivity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
    }

}