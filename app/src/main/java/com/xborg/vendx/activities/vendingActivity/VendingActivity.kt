package com.xborg.vendx.activities.vendingActivity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.activity_vending.*

private var TAG = "VendingActivity"

class VendingActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vending)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        val text ="Test QR code generated" // Whatever you need to encode in the QR code
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qr_code_view.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace();
        }
    }

}