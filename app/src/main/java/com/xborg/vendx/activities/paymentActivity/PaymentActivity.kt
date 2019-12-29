package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment
import org.json.JSONObject
import java.lang.Exception

const val TAG = "PaymentActivity"

class PaymentActivity : FragmentActivity(), PaymentResultListener {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        observerSharedViewModel()
        getDataPassedByMainActivity()
        loadFragments()
    }

    private fun observerSharedViewModel() {

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        sharedViewModel.cartItem.observe(this, Observer { updatedCart ->
            Log.i(TAG, "cartItems updated: $updatedCart")
        })
        sharedViewModel.machineItems.observe(this, Observer { updatedMachineItems ->
            Log.i(TAG, "machineItems updated: $updatedMachineItems")
        })
        sharedViewModel.shelfItems.observe(this, Observer { updatedShelfItems ->
            Log.i(TAG, "shelfItems updated: $updatedShelfItems")
        })
        sharedViewModel.paymentInitiated.observe(this, Observer {intiated ->
            if(intiated) {
                initiatePayment()
            }
        })
    }

    private fun getDataPassedByMainActivity() {

        sharedViewModel.setMachineItemsFromSerializable(intent.getSerializableExtra("machineItems")!!)
        sharedViewModel.setShelfItemsFromSerializable(intent.getSerializableExtra("shelfItems")!!)
        sharedViewModel.setCartItemsFromSerializable(intent.getSerializableExtra("cartItems")!!)
    }

    @SuppressLint("ResourceType")
    private fun loadFragments() {
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

    private fun initiatePayment() {
        Checkout.preload(this)
        startPayment()
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setFullScreenDisable(true)

        try {
            val options = JSONObject()
            options.put("name", "VendX")
            options.put("description", "Reference No. #123456")
            options.put("currency", "INR")
            options.put("amount", sharedViewModel.payableAmount.value!!.toInt().toString() + "00")

            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e(TAG, "Error in starting Razorpay Checkout: $e")
        }

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.e(TAG, "Payment Error: $p0 $p1")
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.e(TAG, "Payment Successful")
    }
}
