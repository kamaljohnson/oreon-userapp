package com.xborg.vendx

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import io.chirp.connect.ChirpConnect
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.MainActivityFragments.HomeFragment
import com.xborg.vendx.MainActivityFragments.ShelfFragment
import com.xborg.vendx.SupportClasses.Item
import com.xborg.vendx.SupportClasses.ItemAdapter

/**
 *  Keys used for chrip lib. visit https://developers.chirp.io/ for details.
*/
const val CHIRP_APP_KEY = "2cc0Afa8DBA2bf4298E7DbB0D";
const val CHIRP_APP_SECRET = "fE11ffF32f195AC3B50c2dFB767A1e9583E5eCdDF68Bccf867";
const val CHIRP_APP_CONFIG = "gHpaLZyR83XICW560DT8fx9VF0M6DP9VM++zm5/GFwA8hOqMVVlWBXdHCKIWW9bUjxOvKdt4gAo8HYy1Y4usnNzctJM7lEcLH5yXzm+F09NqeD2A9miQ2PdjQOzO7mu9RD2wBUSwp0vqVs81T39dHd8Rs4uOOL6kVqZ0WPrz9H8j0Sn5H7ph3qK7B/O2HsKofBw/ztILe2YAwll1sh7OMbPxsj9ClriOzQOrVCu6hShYKFdN6NvW3Bf9lUj14fq5n/nTM5I7rrtQguNz/UlAId4Zx0oaJ0TsK84lo7MY9FvFJHx5fFg2RpRvCJ/a5YPEpZ0OZyyErhVtXFqXlx/8LQhXVJd1OdGuBZtDYYS8wBXoIEObbcCrBw4h7V89n+KKb0Ez//iN9dYgW2N2EyWEgfJOE6WiYlnFA+n/aAwT/KNQXtTqbd289kPqo0lyoPSJtfoUfk4OBtftsczyqoBxiiikFfchYyWVB/Xqhuvn4SoFlfeFqan+/cZdX0AIYkCuLchk2mZgWcR9n3p6TpP9erLcFkNsZXiAB9B87rwDan9has6CckN5VkKCreN/MVRT1YjqLj0k22uhGe9Ive8O3xoLQO7wu7eh9hzk1b4qD6MvQw6J/mpEU27dEHz2oThOU4ZJWooraf6oEzlTjdKprfpZGIpVCYsNBIqqwxDNE4y19aUvde2Qkj5V1kb04RRpvDx/be+AgUR2b4dDZWbNTssd1sZkWQPVPt5erGobw2k=";

private const val REQUEST_RECORD_AUDIO = 1
private const val MIN_CHIRP_VOLUME = 0.3
private const val REQUEST_LOCATION = 42

private const val NUM_PAGES = 2

private var TAG = "MainActivity"

private lateinit var mPager: ViewPager

class MainActivity : FragmentActivity() {
    private lateinit var chirp:ChirpConnect
    private lateinit var parentLayout: View

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            findViewById<TextView>(R.id.location).text = mLastLocation.longitude.toString()
        }
    }

    private lateinit var fragmentContainer: ViewGroup

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

    var temp_items: ArrayList<Item> = ArrayList()

    companion object{
        val items: ArrayList<Item> = ArrayList()               //all the items in the inventory list
        val shelf_items: HashMap<String, Int> = HashMap()               //list of item_ids with count of shelf items
        var cart_items_from_shelf: HashMap<String, Int> = HashMap()
        var cart_items : HashMap<String, Int> = HashMap()        //list of item_ids added to cart along with number of purchases
        var billing_cart : HashMap<String, Int> = HashMap()        //list of item_ids added to cart along with number of purchases
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        parentLayout =  findViewById<View>(android.R.id.content)

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter


        clearCarts()
        getItems()
        getShelfItems()

//        region location

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        getLastLocation()

//        endregion location

        chirp = ChirpConnect(this, CHIRP_APP_KEY, CHIRP_APP_SECRET)
        val error = chirp.setConfig(CHIRP_APP_CONFIG)
        if (error.code == 0) {
            Log.v("ChirpSDK: ", "Configured ChirpSDK")
        } else {
            Log.e("ChirpError: ", error.message)
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

/*
        get_button.setOnClickListener{
            if(cart_items.size == 0) {
                Toast.makeText(this, "Your Cart is Empty", Toast.LENGTH_SHORT).show()
            } else {
                createBillingCart()

                Log.d(TAG, "_______ BILLING CART _______")
                billing_cart.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                if(billing_cart.size == 0) {

                    val intent = Intent(this, VendingActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("cart_items", cart_items)
                    startActivity(intent)

                } else {
                    val order = HashMap<String, Any>()
                    order["UID"] = FirebaseAuth.getInstance().uid.toString()
                    order["Cart"] = billing_cart
                    order["Status"] = "Payment Pending"

                    db.collection("Orders")
                        .add(order)
                        .addOnSuccessListener { orderRef ->
                            Log.d(TAG, "billReference created with ID: ${orderRef.id}")

                            val order_id = orderRef.id

                            val intent = Intent(this, PaymentActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("order_id", order_id)
                            intent.putExtra("cart_items", cart_items)
                            intent.putExtra("billing_cart", billing_cart)
                            startActivity(intent)

                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to place order")
                        }
                }
            }
        }
*/

//        TODO: to be changed to sliding activity change
        /*show_shelf.setOnClickListener{
            if(shelf_items.size == 0) {
                Toast.makeText(this, "Your Shelf is Empty", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ShelfActivity::class.java)
                startActivity(intent)
            }
        }*/

        /*search_text.addTextChangedListener{
            Log.e(TAG, "the searching string is ${it.toString()}")
            if(it.toString().isNotEmpty()) {
                search(it.toString())
            } else {
//                rv_items_list.removeAllViews()
                addItemsToRV(items)
            }
        }*/

        search_button.setOnClickListener{
            search_text.visibility = View.VISIBLE
            search_button.visibility = View.INVISIBLE
            nearby_machine_count_text.visibility = View.INVISIBLE
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

//        region location

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        getLastLocation()

//        endregion location

    }

    override fun onRestart() {
        super.onRestart()
        clearCarts()
        getItems()
        getShelfItems()

//        region location

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        getLastLocation()

//        endregion location
    }

    override fun onPause() {
        super.onPause()
        chirp.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        chirp.stop()
        try {
            chirp.close()
        } catch (e: Exception) {
            e.printStackTrace()
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
            REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Granted. Start getting the location information
                }
            }
        }
    }

    /**
     * sends the message string via audio
     * @param message the message to be send via audio
     */
    private fun sendPayload(message: String) {
        val payload: ByteArray = message.toByteArray()
        val error = chirp.send(payload)
        if (error.code > 0) {
            Log.e("ChirpError: ", error.message)
        } else {
            Log.v("ChirpSDK: ", "Sent $message")
        }
    }

    private fun getItems() {
        db.collection("Inventory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val item = Item()

                    item.item_id = document.id
                    item.name = document.data["Name"].toString()
                    item.cost = document.data["Cost"].toString()
                    item.quantity = document.data["Quantity"].toString()
                    item.item_limit = "0"
                    item.image_src = document.data["Image"].toString()

                    items.add(item)

                }
                addItemsToRV(items)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /**
     * get all the items in the users' shelf
     */
    private fun getShelfItems() {
        var shelfItems: Map<String, Number>

        db.document("Users/$uid")
            .get()
            .addOnSuccessListener { userSnap ->
                if(userSnap.data?.get("Shelf") == null) {
                    Log.d(TAG, "shelf is empty")
                } else {
                    shelfItems = userSnap.data?.get("Shelf") as Map<String, Number>
                    for (shelfItem in shelfItems){
                        Log.d(TAG, shelfItem.key)
                        Log.d(TAG, shelfItem.value.toString())

                        val itemId = shelfItem.key
                        val quantity = shelfItem.value

                        shelf_items[itemId] = quantity.toString().toInt()
                    }
                }
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /**
     * adds all the items to the recycler view
     * as item cards
     */
    private fun addItemsToRV(items: ArrayList<Item>){
//        rv_items_list.layoutManager = LinearLayoutManager(this)
//        rv_items_list.layoutManager = GridLayoutManager(this, 2)
//        rv_items_list.adapter = ItemAdapter(items, this)
    }

    /**
     * calculates the billing_cart using
     *  - cart_items
     *  - shelf_items
     * the billing_cart is used in PaymentActivity
     */
    private fun createBillingCart() {
        Log.d(TAG, "_______ CART ITEMS _______")
        cart_items.forEach{item -> //key: id, value: count

            Log.d(TAG, item.key + " => " + item.value)
            val cart_id = item.key
            val cart_item_count = item.value

            if(shelf_items[cart_id] != null) {

                val shelf_item_count = shelf_items[cart_id]

                if(cart_item_count > shelf_item_count!!) {
                    billing_cart[cart_id] = shelf_items[cart_id]?.let { cart_items[cart_id]?.minus(it) } !!
                    cart_items_from_shelf[cart_id] = shelf_item_count
                } else {
                    cart_items_from_shelf[cart_id] = cart_item_count
                }
            } else {
                billing_cart[cart_id] = cart_item_count
            }
        }
    }

    /**
     * clears
     *  - shelf_items
     *  - cart_items
     *  - cart_items_from_shelf
     *  - billing_cart
     *  - rv_items_list
     * called when activity is
     *  - created
     *  - restarted
     */
    private fun clearCarts() {
        items.clear()
        shelf_items.clear()
        cart_items.clear()
        cart_items_from_shelf.clear()
        billing_cart.clear()
//        rv_items_list.removeAllViews()
    }

    /**
     * checks the permissions required for the activity
     * permissions:
     *  - ACCESS_COARSE_LOCATION
     *  - ACCESS_FINE_LOCATION
     * @return true if PERMISSION_GRANTED else false
     */
    private fun checkPermissions(): Boolean {
        // Locations Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Toast.makeText(this, "location found", Toast.LENGTH_SHORT).show()
                        findViewById<TextView>(R.id.location).text = location.latitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
//    region item search

/*    private fun search(search_name: String) {
        temp_items = ArrayList()
        for (item in items) {
            Log.d(TAG, item.toString())
            var i = 0
            var j = 0
            while(i < item.name.length) {
                if(item.name[i].toUpperCase() == search_name[j].toUpperCase()) {
                    j++
                    if(j == search_name.length) {
                        temp_items.add(item)
                        break
                    }
                }
                i++
            }
            Log.d(TAG, temp_items.size.toString())
        }
        Log.e(TAG, cart_items.toString())
//        rv_items_list.removeAllViews()
        addItemsToRV(temp_items)
    }*/

//    endregion

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {

            return if (position == 0){
                HomeFragment()
            } else {
                ShelfFragment()
            }
        }
    }

    public fun navigate(from: Fragment? = null, to: Fragment, addToBackStack: Boolean = true) {
        if(from != null) from.userVisibleHint = false

        to.userVisibleHint = true

        Log.e(TAG, "the fragment is changed")

        if(from == HomeFragment()) {
            home_button.setBackgroundResource(R.color.fui_transparent)
            shelf_button.setBackgroundResource(R.color.orange)
        } else {
            home_button.setBackgroundResource(R.color.orange)
            shelf_button.setBackgroundResource(R.color.fui_transparent)
        }
        val transaction = supportFragmentManager.beginTransaction()
            .add(fragmentContainer.id, to)
        if(addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}
