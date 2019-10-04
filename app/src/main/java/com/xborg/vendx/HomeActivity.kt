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
const val CHIRP_APP_KEY = "2cc0Afa8DBA2bf4298E7DbB0D";
const val CHIRP_APP_SECRET = "fE11ffF32f195AC3B50c2dFB767A1e9583E5eCdDF68Bccf867";
const val CHIRP_APP_CONFIG = "gHpaLZyR83XICW560DT8fx9VF0M6DP9VM++zm5/GFwA8hOqMVVlWBXdHCKIWW9bUjxOvKdt4gAo8HYy1Y4usnNzctJM7lEcLH5yXzm+F09NqeD2A9miQ2PdjQOzO7mu9RD2wBUSwp0vqVs81T39dHd8Rs4uOOL6kVqZ0WPrz9H8j0Sn5H7ph3qK7B/O2HsKofBw/ztILe2YAwll1sh7OMbPxsj9ClriOzQOrVCu6hShYKFdN6NvW3Bf9lUj14fq5n/nTM5I7rrtQguNz/UlAId4Zx0oaJ0TsK84lo7MY9FvFJHx5fFg2RpRvCJ/a5YPEpZ0OZyyErhVtXFqXlx/8LQhXVJd1OdGuBZtDYYS8wBXoIEObbcCrBw4h7V89n+KKb0Ez//iN9dYgW2N2EyWEgfJOE6WiYlnFA+n/aAwT/KNQXtTqbd289kPqo0lyoPSJtfoUfk4OBtftsczyqoBxiiikFfchYyWVB/Xqhuvn4SoFlfeFqan+/cZdX0AIYkCuLchk2mZgWcR9n3p6TpP9erLcFkNsZXiAB9B87rwDan9has6CckN5VkKCreN/MVRT1YjqLj0k22uhGe9Ive8O3xoLQO7wu7eh9hzk1b4qD6MvQw6J/mpEU27dEHz2oThOU4ZJWooraf6oEzlTjdKprfpZGIpVCYsNBIqqwxDNE4y19aUvde2Qkj5V1kb04RRpvDx/be+AgUR2b4dDZWbNTssd1sZkWQPVPt5erGobw2k=";

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
