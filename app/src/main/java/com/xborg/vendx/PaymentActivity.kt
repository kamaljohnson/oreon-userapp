package com.xborg.vendx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_payment.*
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
        val bill_amount = calculateBill().toString()

        pay_button.setOnClickListener{
            payUsingUpi(amount = bill_amount, upiId = bank_upi_id, name = "kamal", note = "VendX Purchase")
        }

        done_button.setOnClickListener{
            val intent = Intent(this, VendingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        statusListener()
    }

    private fun statusListener() {
        // [START listen_document]
        val docRef = db.collection("Orders").document("${intent.getStringExtra("order_id")}")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                payment_status.text = snapshot.data?.get("Status") as String
            }
        }
        // [END listen_document]
    }

    private fun calculateBill() : Float {
        var bill = 0f

        var table = bill_table

        var row = TableRow(this)
        var col_1_text = TextView(this)
        var col_2_text = TextView(this)
        var col_3_text = TextView(this)

        col_2_text.text = "Cart"
        col_2_text.setTypeface(null, Typeface.BOLD)

        row.addView(col_1_text)
        row.addView(col_2_text)
        row.addView(col_3_text)

        table.addView(row)

        for(cart_item in MainActivity.cart_items) {
            val item_id = cart_item.key
            val item_count = cart_item.value
            for (item in MainActivity.items) {
                if (item.item_id == item_id) {
                    // creating item row and adding to table

                    row = TableRow(this)
                    col_1_text = TextView(this)
                    col_2_text = TextView(this)
                    col_3_text = TextView(this)

                    col_1_text.text = item.name
                    col_1_text.setTypeface(null, Typeface.BOLD)

                    col_2_text.text = item_count.toString()
                    col_2_text.textDirection = View.TEXT_DIRECTION_RTL
                    col_2_text.setTypeface(null, Typeface.BOLD)

                    row.addView(col_1_text)
                    row.addView(col_2_text)
                    row.addView(col_3_text)

                    table.addView(row)
                }
            }
        }

        row = TableRow(this)
        col_2_text = TextView(this)


        col_1_text = TextView(this)
        col_2_text.text = "From Shelf"
        col_2_text.setTypeface(null, Typeface.BOLD)
        col_3_text = TextView(this)

        row.addView(col_1_text)
        row.addView(col_2_text)
        row.addView(col_3_text)
        table.addView(row)

        for(cart_item in MainActivity.cart_items_from_shelf) {
            val item_id = cart_item.key
            val item_count = cart_item.value
            for (item in MainActivity.items) {
                if (item.item_id == item_id) {
                    // creating item row and adding to table

                    row = TableRow(this)
                    col_1_text = TextView(this)
                    col_2_text = TextView(this)
                    col_3_text = TextView(this)

                    col_1_text.text = item.name
                    col_1_text.setTypeface(null, Typeface.BOLD)

                    col_2_text.text = item_count.toString()
                    col_2_text.textDirection = View.TEXT_DIRECTION_RTL
                    col_2_text.setTypeface(null, Typeface.BOLD)

                    row.addView(col_1_text)
                    row.addView(col_2_text)
                    row.addView(col_3_text)

                    table.addView(row)
                }
            }
        }

        row = TableRow(this)
        col_2_text = TextView(this)


        col_1_text = TextView(this)
        col_2_text.text = "Bill"
        col_2_text.setTypeface(null, Typeface.BOLD)
        col_3_text = TextView(this)

        row.addView(col_1_text)
        row.addView(col_2_text)
        row.addView(col_3_text)
        table.addView(row)

        for (cart_item in MainActivity.billing_cart) {
            val item_id = cart_item.key
            val item_count = cart_item.value
            for (item in MainActivity.items) {
                if (item.item_id == item_id) {
                    // creating item row and adding to table

                    row = TableRow(this)
                    col_1_text = TextView(this)
                    col_2_text = TextView(this)
                    col_3_text = TextView(this)


                    col_1_text.text = item.name
                    col_1_text.setTypeface(null, Typeface.BOLD)

                    col_2_text.text = item_count.toString()
                    col_2_text.textDirection = View.TEXT_DIRECTION_RTL
                    col_2_text.setTypeface(null, Typeface.BOLD)

                    col_3_text.text = (item.cost.toFloat() * item_count).toString()
                    col_3_text.textDirection = View.TEXT_DIRECTION_RTL
                    col_3_text.setTypeface(null, Typeface.BOLD)

                    row.addView(col_1_text)
                    row.addView(col_2_text)
                    row.addView(col_3_text)

                    table.addView(row)
                    bill += item.cost.toFloat() * item_count
                    Log.d(TAG, item_id + " -> " + item_count + " -> " + item.cost)
                }
            }
        }

        row = TableRow(this)
        col_1_text = TextView(this)
        col_2_text = TextView(this)
        col_3_text = TextView(this)


        col_1_text.text = "Total"
        col_1_text.setTypeface(null, Typeface.BOLD)

        col_3_text.text = bill.toString()
        col_3_text.textDirection = View.TEXT_DIRECTION_RTL
        col_3_text.setTypeface(null, Typeface.BOLD)

        row.addView(col_1_text)
        row.addView(col_2_text)
        row.addView(col_3_text)

        table.addView(row)

        return bill
    }

    //TODO: call this when Order>Status -> Payment Checked
    private fun onPaymentSuccessful() {
        pay_button.visibility = View.INVISIBLE
        done_button.visibility = View.VISIBLE
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