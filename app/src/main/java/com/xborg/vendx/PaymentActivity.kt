package com.xborg.vendx

import android.content.Context
import android.net.ConnectivityManager
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

class PaymentActivity : AppCompatActivity() {

    private var TAG = "PaymentActivity"
    val db = FirebaseFirestore.getInstance()

    var bank_upi_id: String = ""
    val UPI_PAYMENT:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val order_id: String = intent.getStringExtra("order_id")

        order_id_text.text = order_id
        amount_text.text = calculateBill().toString()
    }

    private fun calculateBill() : Float {
        var bill = 0f

        var i = 0
        for(item in InventoryActivity.cart_items) {
            bill += InventoryActivity.items[i].cost.toFloat() * item.value
            Log.d(TAG, item.value.toString() + " -> " + item.key + " -> " + InventoryActivity.items[i].cost.toFloat())
            i+=1
        }
        return bill
    }
}