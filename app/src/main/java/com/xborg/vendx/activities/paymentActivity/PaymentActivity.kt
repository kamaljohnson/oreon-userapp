package com.xborg.vendx.activities.paymentActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.fragments.addPromotions.AddPromotionsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.cart.CartFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods.PaymentMethodsFragment
import com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus.PaymentStatusFragment
import com.xborg.vendx.activities.vendingActivity.VendingActivity
import com.xborg.vendx.database.PaymentState
import com.xborg.vendx.database.PaymentStatus
import org.json.JSONObject


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

//        sharedViewModel.cartItems.observe(this, Observer { updatedCart ->
//            Log.i(TAG, "cartItems updated: $updatedCart")
//        })
//        sharedViewModel.machineItems.observe(this, Observer { updatedMachineItems ->
//            Log.i(TAG, "machineItems updated: $updatedMachineItems")
//        })
//        sharedViewModel.inventoryItems.observe(this, Observer { updatedInventoryItems ->
//            Log.i(TAG, "inventoryItems updated: $updatedInventoryItems")
//        })

        sharedViewModel.paymentState.observe(this, Observer { currentPaymentState ->
            Log.i(TAG, "Payment State: $currentPaymentState")
            when (currentPaymentState) {
                PaymentState.PaymentInit -> {
                    initiatePayment()
                }
                PaymentState.PaymentComplete -> {
                    Log.i(TAG, "Payment Status : " + sharedViewModel.payment.value!!.Status)
                    if (sharedViewModel.payment.value!!.Status == PaymentStatus.Successful) {
                        Log.i(TAG, "Payment Checked and is authentic")
                    } else {
                        Log.i(TAG, "Payment Checked and is not authentic")
                    }
                }
                PaymentState.PaymentRetry -> {
                    retryPayment()
                }
            }
        })

        sharedViewModel.apiCallError.observe(this, Observer { error ->
            if(error) {
                val oldCount = sharedViewModel.apiCallRetryCount.value!!
                sharedViewModel.apiCallRetryCount.value = oldCount + 1
            }
        })
    }

    private fun getDataPassedByMainActivity() {
        sharedViewModel.setMachineItemsFromSerializable(intent.getSerializableExtra("machineItems")!!)
        sharedViewModel.setInventoryItemsFromSerializable(intent.getSerializableExtra("inventoryItems")!!)
        sharedViewModel.setCartItemsFromSerializableHashMap(intent.getSerializableExtra("cartItems")!!)
    }

    //      region Payment Processing
    private fun initiatePayment() {
        Checkout.preload(this)
        startPayment()
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setFullScreenDisable(true)

        val amount = sharedViewModel.payment.value!!.Amount * 100

        if (amount == 0f) {      //No need of payment
            proceedToVending()
            return
        }

        try {
            val options = JSONObject()
            options.put("name", "VendX")
            options.put("description", "Reference ID. " + sharedViewModel.order.value!!.Id)
            options.put("currency", "INR")
            options.put("amount", amount.toInt().toString())

            val phoneNumber = FirebaseAuth.getInstance().currentUser!!.phoneNumber
            val email = FirebaseAuth.getInstance().currentUser!!.email

            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", phoneNumber)
            options.put("prefill", preFill)

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
        } catch (e: Exception) {
            Log.e(TAG, "error : $e")
        }
    }

    override fun onPaymentSuccess(p0: String?, paymentData: PaymentData) {
        try {
            Log.i(TAG, "payment exited in success_gif")
            sharedViewModel.updatePaymentAfterMakingPayment(
                status = PaymentStatus.SuccessfulLocal,
                razorpayPaymentID = paymentData.paymentId
            )
            loadPaymentStatusFragment()
        } catch (e: Exception) {
            Log.e(TAG, "error : $e")
        }
    }

    private fun retryPayment() {
        finish()
        startActivity(intent)

    }
//      endregion

    //      region Fragment Loading
    @SuppressLint("ResourceType")
    private fun loadFragments() {
        val fragmentManager: FragmentManager = supportFragmentManager
        var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.promotion_fragment_container,
            AddPromotionsFragment(),
            "AddPromotionFragment"
        )
        fragmentTransaction.add(R.id.cart_fragment_container, CartFragment(), "CartFragment")
        fragmentTransaction.add(
            R.id.payment_method_fragment_container,
            PaymentMethodsFragment(),
            "PaymentMethodFragment"
        )
        fragmentTransaction.add(
            R.id.payment_status_text,
            PaymentStatusFragment(),
            "PaymentStatusFragment"
        )
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

    private fun proceedToVending() {
        val intent = Intent(this, VendingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        when (sharedViewModel.paymentState.value) {
            PaymentState.None -> {
                super.onBackPressed()
            }
            PaymentState.OrderInit -> {
                super.onBackPressed()
            }
            PaymentState.PaymentComplete -> {
                super.onBackPressed()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Event Blocked, Processing Request",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}