package com.xborg.vendx

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.firestore.FirebaseFirestore
import io.chirp.connect.ChirpConnect
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.xborg.vendx.MainActivityFragments.HomeFragment
import com.xborg.vendx.MainActivityFragments.ShelfFragment
import com.xborg.vendx.SupportClasses.Item

private const val REQUEST_ENABLE_BT = 2

private const val NUM_PAGES = 2

private var TAG = "MainActivity"

private lateinit var mPager: ViewPager

@Suppress("UNREACHABLE_CODE", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : FragmentActivity() {
    private lateinit var chirp:ChirpConnect
    private lateinit var parentLayout: View

    val db = FirebaseFirestore.getInstance()
    val uid =  FirebaseAuth.getInstance().uid.toString()

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

// region BLUETOOTH SETUP
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "your device does'nt support bluetooth", Toast.LENGTH_SHORT).show()
        } else if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "switched on bluetooth", Toast.LENGTH_SHORT).show()
            bluetoothAdapter.enable()
        } else {
            Toast.makeText(this, "bluetooth is already ON", Toast.LENGTH_SHORT).show()
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
            // Permission has already been granted
            Log.e(TAG, "bluetooth permission already granted")
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

// endregion

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager)

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter

        clearCarts()
        getShelfItems()

        get_button.setOnClickListener{
            if(cart_items.size == 0 && cart_items_from_shelf.size == 0) {
                Toast.makeText(this, "Your Cart is Empty", Toast.LENGTH_SHORT).show()
            } else {
                get_button.isEnabled = false
// region LOGS
                Log.d(TAG, "_______ CART _______")
                cart_items.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                Log.d(TAG, "_______ CART FROM SHELF_______")
                cart_items_from_shelf.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                Log.d(TAG, "_______ BILLING CART _______")
                billing_cart.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                createBillingCart()

                Log.d(TAG, "_______ CART _______")
                cart_items.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                Log.d(TAG, "_______ CART FROM SHELF_______")
                cart_items_from_shelf.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }

                Log.d(TAG, "_______ BILLING CART _______")
                billing_cart.forEach{
                    Log.d(TAG, it.key + " => " + it.value)
                }
// endregion
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

        search_text.setImeOptions(EditorInfo.IME_ACTION_DONE)

        search_text.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                hideSearchBar(search_text.rootView)
                true
            } else {
                false
            }
        }

        search_button.setOnClickListener{
            showSearchBar()
        }

    }

    override fun onRestart() {
        super.onRestart()
        clearCarts()
        getShelfItems()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {

        } else if (bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "switched off bluetooth", Toast.LENGTH_SHORT).show()
            bluetoothAdapter.disable()
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    Log.d(TAG,"device connected \ndevice name: $deviceName\nMAC: $deviceHardwareAddress"
                    )
                }
            }
        }
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
            val count = item.value
            if(cart_items.containsKey(id)) {
                cart_items[id] = cart_items[id]?.plus(count) !!
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
    }

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

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

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

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
