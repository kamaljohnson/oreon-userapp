package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus.PaymentStatusFragment
import com.xborg.vendx.database.PaymentState
import com.xborg.vendx.database.PaymentStatus
import org.json.JSONObject
import java.lang.Exception

const val TAG = "PaymentActivity"

class PaymentActivity : FragmentActivity(), PaymentResultWithDataListener {

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

        sharedViewModel.paymentState.observe(this, Observer { currentPaymentState ->
            Log.i(TAG, "Payment State: $currentPaymentState")
            when(currentPaymentState) {
                PaymentState.PaymentInit -> {
                    initiatePayment()
                }
                PaymentState.PaymentFinished -> {
                    if(sharedViewModel.payment.value!!.status == PaymentStatus.Successful) {
                        Log.i(TAG, "Payment Checked and is authentic")
                    } else {
                        Log.i(TAG, "Payment Checked and is not authentic")
                    }
                }
            }
        })
    }

    private fun getDataPassedByMainActivity() {

        sharedViewModel.setMachineItemsFromSerializable(intent.getSerializableExtra("machineItems")!!)
        sharedViewModel.setShelfItemsFromSerializable(intent.getSerializableExtra("shelfItems")!!)
        sharedViewModel.setCartItemsFromSerializable(intent.getSerializableExtra("cartItems")!!)
    }

//      region Payment Processing
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
            options.put("description", "Reference ID. " + sharedViewModel.order.value!!.id)
            options.put("currency", "INR")
            options.put("amount", sharedViewModel.payment.value!!.amount.toInt().toString() + "00")

            checkout.open(this, options)
        } catch (e: Exception) {

            Log.e(TAG, "Error in starting Razorpay Checkout: $e")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, paymentData: PaymentData) {
        try {
            Log.i(TAG, "payment exited in error")
            sharedViewModel.updatePaymentAfterMakingPayment(
                status = PaymentStatus.Failed,
                razorpayPaymentID = null
            )
            loadPaymentStatusFragment()
        } catch(e: Exception) {
            Log.e(TAG, "error : $e")
        }
    }

    override fun onPaymentSuccess(p0: String?, paymentData: PaymentData) {
        try {
            sharedViewModel.updatePaymentAfterMakingPayment(
                status = PaymentStatus.SuccessfulLocal,
                razorpayPaymentID = paymentData.paymentId
            )
            loadPaymentStatusFragment()
        } catch (e: Exception) {
            Log.e(TAG, "error : $e")
        }
    }

//      endregion

//      region Fragment Loading
    @SuppressLint("ResourceType")
    private fun loadFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.promotion_fragment_container, AddPromotionsFragment(), "AddPromotionFragment")
        fragmentTransaction.add(R.id.cart_fragment_container, CartFragment(), "CartFragment")
        fragmentTransaction.add(R.id.payment_method_fragment_container, PaymentMethodsFragment(), "PaymentMethodFragment")
        fragmentTransaction.add(R.id.payment_status_container, PaymentStatusFragment(), "PaymentStatusFragment")
        fragmentTransaction.commitNowAllowingStateLoss()

        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(fragmentManager.findFragmentByTag("PaymentStatusFragment")!!)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    private fun loadPaymentStatusFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.show(fragmentManager.findFragmentByTag("PaymentStatusFragment")!!)
        fragmentTransaction.commitNowAllowingStateLoss()
    }
//      endregion
}
