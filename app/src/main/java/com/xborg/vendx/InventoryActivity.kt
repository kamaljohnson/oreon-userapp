package com.xborg.vendx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class InventoryActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    private var TAG = "InventoryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)
        var mid: String = intent.getStringExtra("mid")
        if(mid == "") {
            getAllItems()
        } else {
            getAllItems(mid)
        }
    }

    //get item inside machine mid
    private fun getAllItems(mid:String){

    }

    //get all the items
    private fun getAllItems() {
        // [START get_all_users]
        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}
