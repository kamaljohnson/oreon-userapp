package com.xborg.vendx

import android.Manifest
import android.app.PendingIntent.getActivity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.chirp.connect.ChirpConnect
import io.chirp.connect.interfaces.ConnectEventListener
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

/**
 *  Keys used for chrip lib. visit https://developers.chirp.io/ for details.
*/
const val CHIRP_APP_KEY = "2cc0Afa8DBA2bf4298E7DbB0D"
const val CHIRP_APP_SECRET = "fE11ffF32f195AC3B50c2dFB767A1e9583E5eCdDF68Bccf867"
const val CHIRP_APP_CONFIG = "C51hD/MFhXn30GQdn+4I/EviethgtmRVTZcLUDAeGgSmYlim5GDm9v9gFFJuK0YmJSkXrBaLBv/miBtYMG83KHe3Rd9a8k2Lkk7fV3HZ+O3S+weDI+3iEI5xzoprL9v/gF/FfniTneJDFdN9KI6NcTwrc54wmy3mL8WRwuo9HGGuPwcrzohQ/V3QfAS2YECGwuOFXbqrYSlZPzqD7B3qiLd3cMww+YXJUg7XoTiDEBX8xDGqgrhXi0D74MqsKRxDRcQJoGG+tbuV8ZQcTe0CnfFduE9/ukzEM3uqWD7kZ49hd2s0iPogK92knIB6zrImIMOUGE2QdC4ltPHS3QbpXn3s5lLrkJRz4QUYUp7KFuPbiHo7yrX9pTwujx6LahDntqHseSR3kG7PCXKkk5zHvG0k0hrtuzymSczo9TZg+dL5/o0SPBFwxqbociX63OBBEW498XY1X/Z1CszJ1Yb196NpnQgo4P92CRcKtOrA6K4MR/2CGB+nowEdNo5cuBWyu/HGdpnFpQTptHytB9q3ogeVngTGFMNyEXNaMYSrOwODoFNBWNF1P+9LZQNrtGtptxvLGDmZMQd3dQxTeAB6VTpxWngi+WxB+jxgQ8tJ3uoUEPL+gOMi9ExjgtN7oh1kzvbAv80Kd2GdmnLkPLObbKPGt/IuXAy7gRQLeV/Xnbr+j9sHRIeRDCPYsLCGM7OpSuLNjEWYl2z2yKXfV1Ji1fcwWW3Rmx5CcnYTIC9hqKa8cdYmmEqZOngmsJIYsjoUMnLZ00uSa40u4RdZ+Q5PcDIaUcs2w0mMkpERYQ2qrBQzIdnQZmtEPRInuHH6IdK55wIdLD9pyNho3PQ+jiGl0uMtxURDjdySOw+CsyvXc/tBI3vXhomyXhOOd4AsUOug2hdM6lFyLIyXX4PvIRiOZg33WMJc75ph170Bc6e9Vm7eI9ziZdImrTCssRqI3EAZgn+Yrb2Stmu2OrjlQHvzGDqjtvfhaE9PblFCzwu2+3ZRutBuAjy8PMAtXyyPXgf+b31WjTK68T2vPd3Rm9ITgK9KtObuW7uj76QpbbFuKsWsvsfWJ/DXoOOT7tzTXnuHmKgTK0YWOdqc9f4Y8bOuC6ZThKh4SozajJ0ira90GSA="
// standard chirp (high blueky audio)
//const val CHIRP_APP_KEY = "f6Daa8AB9544feb32A3454aBC"
//const val CHIRP_APP_SECRET = "E1F1C5AFcEe7B5CEd6Da5b1de48Dcf00Dc745a0eb4ff69B97B"
//const val CHIRP_APP_CONFIG = "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"
private const val REQUEST_RECORD_AUDIO = 1
private const val MIN_CHIRP_VOLUME = 0.3

class HomeActivity : AppCompatActivity() {

    private lateinit var chirp:ChirpConnect
    private lateinit var parentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        parentLayout =  findViewById<View>(android.R.id.content)

        chirp = ChirpConnect(this, CHIRP_APP_KEY, CHIRP_APP_SECRET)
        val error = chirp.setConfig(CHIRP_APP_CONFIG)
        if (error.code == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK")
        } else {
            Log.e("ChirpError: ", error.message)
        }

        home_vent_btn.setOnClickListener { view ->
            status_txt.text = chirp.version
            sendPayload()
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
        }
        chirp.onReceived { payload: ByteArray?, channel: Int ->
            if (payload != null) {
                val identifier = String(payload)
                Toast.makeText(applicationContext, "data : $identifier",Toast.LENGTH_LONG).show()
                Log.v("ChirpSDK: ", "Received :$identifier")
            } else {
                Log.e("ChirpError: ", "Decode failed")
            }
        }
        chirp.onSystemVolumeChanged { oldVolume: Float, newVolume: Float ->
            if (newVolume < MIN_CHIRP_VOLUME){
                val snackbar = Snackbar.make(parentLayout, "low volume, transmission may fail", Snackbar.LENGTH_SHORT)
                val snackView = snackbar.view
                snackView.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.warning))
                snackbar.show()
            }
//            else if(oldVolume-newVolume>0) {
//                val snackBar = Snackbar.make(parentLayout, "volume changed to: $newVolume", Snackbar.LENGTH_SHORT)
//                snackBar.setAction("CLOSE") { }
//                    .setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
//                    .show()
//            }
            Log.v("Chirp", "volume changed")
        }
    }


    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
        } else {
            // Start ChirpSDK sender and receiver, if no arguments are passed both sender and receiver are started
            val error = chirp.start(send = true, receive = true)
            if (error.code > 0) {
                Log.e("ChirpError: ", error.message)
            } else {
                Log.v("ChirpSDK: ", "Started ChirpSDK")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val error = chirp.start()
                    if (error.code > 0) {
                        Log.e("ChirpError: ", error.message)
                    } else {
                        Log.v("ChirpSDK: ", "Started ChirpSDK")
                    }
                }
                return
            }
        }
    }

    private fun sendPayload() {
        home_vent_btn.isClickable = false
        val identifier = input_data.text.toString()
        val payload: ByteArray = identifier.toByteArray()
        val error = chirp.send(payload)
        if (error.code > 0) {
            Log.e("ChirpError: ", error.message)
        } else {
            Log.v("ChirpSDK: ", "Sent $identifier")
        }
        home_vent_btn.isClickable = true
    }

    override fun onPause() {
        super.onPause()
        chirp.stop()
    }

    // Release memmory reserved by Chirp SDK
    override fun onDestroy() {
        super.onDestroy()
        chirp.stop()
        try {
            chirp.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
