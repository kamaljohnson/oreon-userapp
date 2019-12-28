package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment

const val TAG = "PaymentActivity"

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val cartItemsString = intent.getStringExtra("cartItems")
        Log.i(TAG, cartItemsString)
        loadInitialFragments()
    }

    @SuppressLint("ResourceType")
    private fun loadInitialFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val addPromotionsFragment = AddPromotionsFragment()
        val cartFragment = CartFragment()
        val paymentMethodsFragment = PaymentMethodsFragment()

        fragmentTransaction.add(R.id.promotion_fragment_container, addPromotionsFragment)
        fragmentTransaction.add(R.id.cart_fragment_container, cartFragment)
        fragmentTransaction.add(R.id.payment_method_fragment_container, paymentMethodsFragment)
        fragmentTransaction.commit()

    }
}
