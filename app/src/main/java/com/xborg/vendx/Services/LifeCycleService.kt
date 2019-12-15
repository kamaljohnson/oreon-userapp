package com.xborg.vendx.Services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.db


class LifeCycleService : Service() {

    private var TAG = "LifeCycleService"

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onTaskRemoved(rootIntent: Intent?) { //unregister listeners
        //do any other cleanup if required
        //stop service
        Log.e(TAG, "app closed")
        val uid =  FirebaseAuth.getInstance().uid.toString()
        db.collection("Users").document(uid)
            .update("Status", "Offline")
            .addOnSuccessListener {
            }
            .addOnFailureListener{
            }
        BluetoothAdapter.getDefaultAdapter().disable()
        stopSelf()
    }
}
