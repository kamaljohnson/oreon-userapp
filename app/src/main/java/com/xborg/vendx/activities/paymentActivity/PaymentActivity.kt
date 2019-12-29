package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment
import com.xborg.vendx.adapters.ItemCardAdapter
import com.xborg.vendx.adapters.ItemCartSlipAdapter
import kotlinx.android.synthetic.main.fragment_cart.*

const val TAG = "PaymentActivity"

class PaymentActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        observerSharedViewModel()

        getDataPassedByMainActivity()
        loadInitialFragments()
    }

    private fun observerSharedViewModel() {

        sharedViewModel.cartItem.observe(this, Observer { updatedCart ->
            Log.i(TAG, "cartItems updated: $updatedCart")
        })
        sharedViewModel.machineItems.observe(this, Observer { updatedMachineItems ->
            Log.i(TAG, "machineItems updated: $updatedMachineItems")

            updateCartItemsToRV()
        })
        sharedViewModel.shelfItems.observe(this, Observer { updatedShelfItems ->
            Log.i(TAG, "shelfItems updated: $updatedShelfItems")
        })

    }

    private fun updateCartItemsToRV() {

        rv_cart.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ItemCartSlipAdapter(sharedViewModel.cartItem.value!!, context)
        }
    }

    private fun getDataPassedByMainActivity() {

        sharedViewModel.setMachineItemsFromSerializable(intent.getSerializableExtra("machineItems")!!)
        sharedViewModel.setShelfItemsFromSerializable(intent.getSerializableExtra("shelfItems")!!)
        sharedViewModel.setCartItemsFromSerializable(intent.getSerializableExtra("cartItems")!!)
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
