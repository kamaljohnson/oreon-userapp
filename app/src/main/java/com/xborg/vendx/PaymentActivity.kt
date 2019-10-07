package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val amount: Float = intent.getFloatExtra("amount", 0f)
        val order_id: String = intent.getStringExtra("order_id")

        order_id_text.text = order_id
        amount_text.text = amount.toString()
    }
}
