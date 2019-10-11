package com.xborg.vendx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_payment.*
import java.security.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class PaymentActivity : AppCompatActivity() {

    private var TAG = "PaymentActivity"
    val db = FirebaseFirestore.getInstance()

    var bank_upi_id: String = "7012043162@ybl"
    val UPI_PAYMENT:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val order_id: String = intent.getStringExtra("order_id")

        order_id_text.text = order_id
        amount_text.text = calculateBill().toString()


        pay_button.setOnClickListener{
            payUsingUpi(amount = amount_text.text.toString(), upiId = bank_upi_id, name = "kamal", note = "VendX Purchase")
        }
    }

    private fun calculateBill() : Float {
        var bill = 0f

        for(cart_item in InventoryActivity.cart_items) {
            val item_id = cart_item.key
            val item_count = cart_item.value
            for(item in InventoryActivity.items) {
                if(item.item_id == item_id) {
                    bill += item.cost.toFloat() * item_count
                    Log.d(TAG, item_id + " -> " + item_count + " -> " + item.cost)
                }
            }
        }
        return bill
    }

    //TODO: call this when Order>Status -> Payment Checked
    private fun onPaymentSuccessful() {
        val intent = Intent(this, VendShelfActivity::class.java)
        startActivity(intent)
    }

    private fun payUsingUpi(amount:String, upiId:String, name:String, note:String) {

        var uri: Uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", name)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", amount)
            .appendQueryParameter("cu", "INR")
            .build()

        val upiPayIntent : Intent = Intent(Intent.ACTION_VIEW)
        upiPayIntent.setData(uri)
        val chooser:Intent = Intent.createChooser(upiPayIntent, "Pay with")

        if(chooser.resolveActivity(packageManager) != null){
            startActivityForResult(chooser, UPI_PAYMENT)
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_LONG).show()
        }
    }
    //D/UPI: onActivityResult: txnId=AXI23ae9611d45f4c9ca6d0fdcfdb61ee6f&responseCode=00&Status=SUCCESS&txnRef=912414555047
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("UPI", resultCode.toString()) //when user simply back without payment
        if (Activity.RESULT_OK == resultCode || resultCode === 11 || requestCode == 777) {
            if (data != null) {
                val trxt = data.getStringExtra("response")
                Log.d("UPI", "onActivityResult: $trxt")
                val dataList = ArrayList<String>()
                dataList.add(trxt)
                upiPaymentDataOperation(dataList)
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null")
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        } else {
            Log.d("UPI", "onActivityResult: " + "Return data is null") //when user simply back without payment
            val dataList = ArrayList<String>()
            dataList.add("nothing")
            upiPaymentDataOperation(dataList)
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@PaymentActivity)) {
            var str = data[0]
            Log.d("UPIPAY", "upiPaymentDataOperation: $str")
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }

            if (status == "success") {
                //Code to handle successful transaction here.
                Toast.makeText(this@PaymentActivity, "Payment successful.", Toast.LENGTH_SHORT).show()

                db.collection("Orders").document("${order_id_text.text}")
                    .update("Status", "Payment Completed")
                    .addOnSuccessListener {
                        Log.d(TAG, "Status : Payment Completed")
                        onPaymentSuccessful()
                    }
                    .addOnFailureListener{
                        Log.d(TAG, "Failed to update Status")
                    }
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@PaymentActivity, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()

                db.collection("Orders").document("${order_id_text.text}")
                    //TODO: change to Payment Cancelled after phone UPI check working
                    .update("Status", "Payment Complete")
                    .addOnSuccessListener {
                        Log.d(TAG, "Status : Payment Cancelled")
                        //TODO: remove this after phone UPI check working
                        onPaymentSuccessful()
                    }
                    .addOnFailureListener{
                        Log.d(TAG, "Failed to update Status")
                    }
            } else {

            }
        } else {
            Toast.makeText(
                this@PaymentActivity,
                "Internet connection is not available. Please check and try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val netInfo = connectivityManager.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected
                && netInfo.isConnectedOrConnecting
                && netInfo.isAvailable
            ) {
                return true
            }
        }
        return false
    }
}