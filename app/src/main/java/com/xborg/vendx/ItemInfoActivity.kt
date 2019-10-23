package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_item_info.*

private var TAG = "ItemInfoActivity"

class ItemInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)
        val item_id: String = intent.getStringExtra("item_id")
        loadItemInfo(item_id)
    }

    private fun loadItemInfo(item_id: String) {
        db.document("Inventory/$item_id")
            .get()
            .addOnSuccessListener { document ->

                name.text = document.data!!["Name"].toString()
                cost.text = document.data!!["Cost"].toString()

                var image_src = document.data!!["Image"].toString()

                Glide
                    .with(this)
                    .load(image_src)
                    .into(image)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}
