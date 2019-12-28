package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        loadInitialFragments()
    }

    @SuppressLint("ResourceType")
    private fun loadInitialFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager

        val addPromotionsFragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val addPromotionsFragment = AddPromotionsFragment()
        addPromotionsFragmentTransaction.add(R.id.promotion_fragment_container, addPromotionsFragment)
        addPromotionsFragmentTransaction.commit()

        val cartFragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val cartFragment = CartFragment()
        cartFragmentTransaction.add(R.id.cart_fragment_container, cartFragment)
        cartFragmentTransaction.commit()

        val paymentFragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val paymentMethodsFragment = PaymentMethodsFragment()
        paymentFragmentTransaction.add(R.id.payment_method_fragment_container, paymentMethodsFragment)
        paymentFragmentTransaction.commit()
    }
}
