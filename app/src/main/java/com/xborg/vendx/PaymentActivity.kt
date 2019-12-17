package com.xborg.vendx

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.Bluetooth.BluetoothConnectionActivity
import kotlinx.android.synthetic.main.activity_payment.*
import kotlin.collections.ArrayList

class PaymentActivity : AppCompatActivity() {

    private var TAG = "PaymentActivity"
    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    var bank_upi_id: String = "7012043162@ybl"
    val UPI_PAYMENT:Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val order_id: String = intent.getStringExtra("order_id")

        order_id_text.text = order_id
        val bill_amount = calculateBill().toString()

        Log.e(TAG, "cart:\t\t\t" + MainActivity.cart_items)
        Log.e(TAG, "billing cart:\t\t" + MainActivity.billing_cart)
        Log.e(TAG, "cart from shelf :\t " + MainActivity.cart_items_from_shelf)

        pay_button.setOnClickListener{
            payUsingUpi(amount = bill_amount, upiId = bank_upi_id, name = "kamal", note = "VendX Purchase")
            pay_button.isEnabled = false
            progressBar.visibility = View.VISIBLE
            status.text = "processing payment"
        }

        get_now_button.setOnClickListener{
            val intent = Intent(this, BluetoothConnectionActivity::class.java)
            startActivity(intent)
        }

        done_button.setOnClickListener{
            db.collection("Users").document(uid)
                .update("Bag", null)
                .addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Log.d(TAG, "Failed to update Status")
                }
        }

        statusListener()
    }

    override fun onResume() {
        super.onResume()
        MainActivity.user_state = States.CHECKOUT
        Log.e(TAG, "user_state: " + MainActivity.user_state)
    }

    @SuppressLint("DefaultLocale")
    private fun statusListener() {
        // [START listen_document]
        val docRef = db.collection("Orders").document("${intent.getStringExtra("order_id")}")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                if((snapshot.data?.get("Status") as String).toLowerCase() == "payment complete") {
                    status.text = "payment successful"
                    onPaymentSuccessful()
                }
            }
        }
        // [END listen_document]
    }

    @SuppressLint("ResourceAsColor")
    private fun calculateBill() : Float {
        var bill = 0f

        var table = bill_table

        var row: TableRow
        var col_1_text: TextView
        var col_2_text: TextView
        var col_3_text: TextView

        for(item in MainActivity.cart_items_from_shelf) {
            val item_id = item.key
            val item_count = item.value
            for (item in MainActivity.items) {
                if (item.item_id == item_id) {
                    // creating item_card row and adding to table

                    row = TableRow(this)
                    col_1_text = TextView(this)
                    col_2_text = TextView(this)
                    col_3_text = TextView(this)

                    col_1_text.text = item.name

                    col_2_text.text = item_count.toString()
                    col_2_text.textDirection = View.TEXT_DIRECTION_RTL

                    row.addView(col_1_text)
                    row.addView(col_2_text)
                    row.addView(col_3_text)
                    row.minimumHeight = 80

                    table.addView(row)
                }
            }
        }

        for (item in MainActivity.billing_cart) {
            val item_id = item.key
            val item_count = item.value
            for (item in MainActivity.items) {
                if (item.item_id == item_id) {
                    // creating item_card row and adding to table

                    row = TableRow(this)
                    col_1_text = TextView(this)
                    col_2_text = TextView(this)
                    col_3_text = TextView(this)


                    col_1_text.text = item.name

                    col_2_text.text = item_count.toString()
                    col_2_text.textDirection = View.TEXT_DIRECTION_RTL

                    col_3_text.text = (item.cost.toFloat() * item_count).toString()
                    col_3_text.textDirection = View.TEXT_DIRECTION_RTL

                    row.addView(col_1_text)
                    row.addView(col_2_text)
                    row.addView(col_3_text)
                    row.minimumHeight = 80

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

        col_3_text.text = bill.toString()
        col_3_text.textDirection = View.TEXT_DIRECTION_RTL

        row.addView(col_1_text)
        row.addView(col_2_text)
        row.addView(col_3_text)
        row.minimumHeight = 100

        table.addView(row)

        return bill
    }

    //TODO: call this when Order>Status -> Payment Checked
    private fun onPaymentSuccessful() {
        get_now_button.visibility = View.VISIBLE
        done_button.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        successAnimation.visibility = View.VISIBLE
        MainActivity.user_state = States.PAY_SUCCESS
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
            pay_button.visibility = View.INVISIBLE
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
//                    TODO: uncomment this when actually acepting payments
//                    pay_button.text = "Retry"
//                    pay_button.visibility = View.VISIBLE
                }
            }

            when {
                status == "success" -> {
                    //Code to handle successful transaction here.
                    Toast.makeText(this@PaymentActivity, "Payment successful.", Toast.LENGTH_SHORT).show()

                    db.collection("Orders").document("${order_id_text.text}")
                        .update("Status", "~Payment Completed")
                        .addOnSuccessListener {
                            Log.d(TAG, "Status : Payment Completed")
                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to update Status")
                        }
                }
                "Payment cancelled by user." == paymentCancel -> {
                    Toast.makeText(this@PaymentActivity, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()

                    db.collection("Orders").document("${order_id_text.text}")
                        //TODO: change to Payment Cancelled after phone UPI check working
                        .update("Status", "~Payment Complete")
                        .addOnSuccessListener {
                            Log.d(TAG, "Status : Payment Cancelled")
                            //TODO: remove this after phone UPI check working
                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to update Status")
                        }
                }
                else -> {

                }
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