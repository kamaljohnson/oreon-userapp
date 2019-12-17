package com.xborg.vendx

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.xborg.vendx.MainActivityFragments.HomeFragment
import com.xborg.vendx.MainActivityFragments.ShelfFragment
import com.xborg.vendx.SupportClasses.Item

private const val REQUEST_ENABLE_BT = 2
private const val REQUEST_ENABLE_LOC = 3

private const val NUM_PAGES = 2

private var TAG = "MainActivity"

private lateinit var mPager: ViewPager

@Suppress("UNREACHABLE_CODE", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "UNUSED_ANONYMOUS_PARAMETER"
)
class MainActivity : FragmentActivity() {

    private lateinit var parentLayout: View

    val db = FirebaseFirestore.getInstance()
    lateinit var functions: FirebaseFunctions

    val uid =  FirebaseAuth.getInstance().uid.toString()

    companion object{
        var items: ArrayList<Item> = ArrayList()               //all the items in the inventory list
        val shelf_items: HashMap<String, Int> = HashMap()               //list of item_ids with count of shelf items
        var cart_items_from_shelf: HashMap<String, Int> = HashMap()
        var cart_items : HashMap<String, Int> = HashMap()        //list of item_ids added to cart along with number of purchases
        var billing_cart : HashMap<String, Int> = HashMap()        //list of item_ids added to cart along with number of purchases

        var get_button_lock : Boolean = false

        lateinit var cart_item_count: TextView
        lateinit var get_button: Button

        lateinit var fusedLocationClient: FusedLocationProviderClient

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        parentLayout =  findViewById<View>(android.R.id.content)
        functions = FirebaseFunctions.getInstance()

// region BLUETOOTH SETUP
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Your device does not support bluetooth," +
                                "please check with your local dealer and " +
                                "try again")
                .setPositiveButton(R.string.Ok) { _, _ ->

                }
            builder.create()
            builder.show()
        } else if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            //bluetooth is already switched on
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            Log.e(TAG, "bluetooth permission not granted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_ENABLE_BT)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e(TAG, "bluetooth permission already granted")
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("We need to access location to find vending machines near you")
                .setPositiveButton(R.string.Ok) { _, _ ->
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_ENABLE_LOC)
                }
            builder.create()
            builder.show()
        }
// endregion
//        region NEARBY MACHINES
//        var lm: LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        var locEnabled = false
//
//        try {
//            locEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
//                LocationManager.NETWORK_PROVIDER)
//        } catch (ex: Exception) {
//        }
//        if(locEnabled) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if(location != null) {
//                    getNearbyMachines(location)
//                }
//            }
//        } else {
//            Log.e(TAG, "location disabled")
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage("Please switch on location to access nearby vending machines")
//                .setPositiveButton(R.string.Ok) { _, _ ->
//                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    startActivity(intent)
//                }
//            builder.create()
//            builder.show()
//        }
//        closestMachineUpdateListener()
//      endregion

        mPager = findViewById(R.id.pager)

        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        clearCarts()
        getShelfItems()

        search_text.imeOptions = EditorInfo.IME_ACTION_DONE

        search_text.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                hideSearchBar(search_text.rootView)
                true
            } else {
                false
            }
        }

        get_button.setOnClickListener{
            if(cart_items.size == 0 && cart_items_from_shelf.size == 0) {
                Toast.makeText(this, "Your Cart is Empty", Toast.LENGTH_SHORT).show()
            } else {
                get_button_lock = true
                get_button.isEnabled = false

                createBillingCart()

                if(billing_cart.size == 0) {
                    val order = HashMap<String, Any>()
                    order["UID"] = FirebaseAuth.getInstance().uid.toString()
                    order["Billing_Cart"] = billing_cart
                    order["Cart"] = cart_items
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
                            startActivity(intent)

                        }
                        .addOnFailureListener{
                            Log.d(TAG, "Failed to place order")
                        }

                } else {
                    val order = HashMap<String, Any>()
                    order["UID"] = FirebaseAuth.getInstance().uid.toString()
                    order["Billing_Cart"] = billing_cart
                    order["Cart"] = cart_items
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

        search_button.setOnClickListener{
            showSearchBar()
        }

    }

    override fun onRestart() {
        super.onRestart()
//        clearCarts()
//        getShelfItems()
    }

    override fun onResume() {
        super.onResume()
        get_button_lock = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.history -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
                    Log.e(TAG, userSnap.data?.get("Shelf").toString())
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
     * calculates the billing_cart using
     *  - cart_items
     *  - shelf_items
     * the billing_cart is used in PaymentActivity
     */
    private fun createBillingCart() {
        cart_items.forEach { item ->    //key: id, value: count
            val id = item.key
            val count = item.value
            billing_cart[id] = count
        }

        cart_items_from_shelf.forEach { item ->    //key: id, value: count
            val id = item.key
            var count = item.value
            if(cart_items.containsKey(id)) {
                count += cart_items[id]!!
            }
            cart_items[id] = count
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
        Log.e(TAG, "called clear cart")
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = NUM_PAGES

        @SuppressLint("ResourceAsColor")
        override fun getItem(position: Int): Fragment {

            return if (position == 0){
                HomeFragment()
            } else {
                ShelfFragment()
            }
        }
    }

    private fun hideSearchBar(view: View) {
        search_text.visibility = View.INVISIBLE
        search_button.visibility = View.VISIBLE
        nearby_machine_count_text.visibility = View.VISIBLE

        hideKeyboard(view)
    }

    private fun showSearchBar() {
        search_text.visibility = View.VISIBLE
        search_button.visibility = View.INVISIBLE
        nearby_machine_count_text.visibility = View.INVISIBLE
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            REQUEST_ENABLE_LOC -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("You had denied access to location before, please proceed to settings " +
                            "and grand permission to location")
                        .setPositiveButton(R.string.Ok) { _, _ ->
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                            startActivity(intent)
                        }
                    builder.create()
                    builder.show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
        }
    }

    private fun getNearbyMachines(location: Location): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "longitude" to location.longitude,
            "latitude" to location.latitude,
            "push" to true
        )

        return functions
            .getHttpsCallable("getClosestMachines")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }

    @SuppressLint("DefaultLocale")
    private fun closestMachineUpdateListener() {
        // [START listen_document]
        val docRef = db.collection("Users").document(uid)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val closestMachines = snapshot.data?.get("ClosestMachines") as ArrayList<*>
                val machineCount = closestMachines.size
                if(machineCount > 0) {
                    nearby_machine_count_text.text = machineCount.toString()
                    closest_machine_name_text.text = closestMachines[0].toString()
                    nearby_machine_count_text.visibility = View.VISIBLE
                    scroll_icon.visibility = View.VISIBLE
                }
            }
        }
        // [END listen_document]
    }
}