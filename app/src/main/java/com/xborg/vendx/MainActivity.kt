package com.xborg.vendx

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.xborg.vendx.MainActivityFragments.HomeFragment
import com.xborg.vendx.MainActivityFragments.ShelfFragment
import com.xborg.vendx.MainActivityFragments.ShopFragment
import com.xborg.vendx.models.ItemModel
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_ENABLE_BT = 2
private const val REQUEST_ENABLE_LOC = 3

private var TAG = "MainActivity"

private var mLayout: SlidingUpPanelLayout? = null

enum class States {
    NEW_SELECT,
    CONTINUE_SELECT,
    CHECKOUT,
    PAY_INIT,
    PAY_SUCCESS,
    VEND
}

enum class Fragments {
    HOME,
    SHOP,
    SHELF
}

@Suppress("UNREACHABLE_CODE", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "UNUSED_ANONYMOUS_PARAMETER", "UNCHECKED_CAST"
)
class MainActivity : FragmentActivity() {

    val db = FirebaseFirestore.getInstance()
    lateinit var functions: FirebaseFunctions

    companion object{
        var items: ArrayList<ItemModel> = ArrayList()               //all the items in the inventory list
        var cart_items_from_shelf: HashMap<String, Int> = HashMap()
        var cart_items : HashMap<String, Int> = HashMap()          //list of item_ids added to cart along with number of purchases
        var billing_cart : HashMap<String, Int> = HashMap()        //list of item_ids added to cart along with number of purchases

        var get_button_lock : Boolean = false

        var user_state: States = States.NEW_SELECT
        var current_fragment: Fragments = Fragments.HOME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBottomNavigationView()
        initBottomSwipeUpView()

        functions = FirebaseFunctions.getInstance()

// region Bluetooth Setup
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
//  endregion

        clearCarts()

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
                    order["Status"] = "From Shelf"

                    db.collection("Orders")
                        .add(order)
                        .addOnSuccessListener { orderRef ->
                            Log.d(TAG, "billReference created with ID: ${orderRef.id}")

                            val order_id = orderRef.id

                            val intent = Intent(this, PaymentActivity::class.java)
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

    }

    override fun onResume() {
        super.onResume()
        get_button_lock = false
        when(user_state) {
            States.CHECKOUT -> {
                user_state = States.CONTINUE_SELECT
            }
            else -> {

            }
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
        cart_items.clear()
        cart_items_from_shelf.clear()
        billing_cart.clear()
        Log.e(TAG, "called clear cart")
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

//    region Activity Support functions
    private fun initBottomNavigationView() {

        val bottomNavigation = findViewById<BottomNavigationViewEx>(R.id.bottom_navigation)
        bottomNavigation.enableAnimation(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.setTextVisibility(false)

        changeFragment(HomeFragment(), "HomeFragment")

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home-> {
                    current_fragment = Fragments.HOME
                    changeFragment(HomeFragment(), "HomeFragment")
                    showActionButton()
                    showSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_shop-> {
                    current_fragment = Fragments.SHOP
                    changeFragment(ShopFragment(), "ShopFragment")
                    hideActionButton()
                    hideSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_shelf-> {
                    current_fragment = Fragments.SHELF
                    changeFragment(ShelfFragment(), "ShelfFragment")
                    showActionButton()
                    showSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun changeFragment(fragment: Fragment, tagFragmentName: String) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val currentFragment: Fragment? = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var tempFragment: Fragment? = fragmentManager.findFragmentByTag(tagFragmentName)
        if(tempFragment == null) {
            tempFragment = fragment
            fragmentTransaction.add(R.id.fragment_container, tempFragment, tagFragmentName)
        } else {
            fragmentTransaction.show(tempFragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(tempFragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()

    }

    private fun initBottomSwipeUpView() {
        mLayout = findViewById(R.id.bottom_slide_up_container)
        mLayout!!.anchorPoint = 0.2f

        mLayout!!.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if(slideOffset > 0.05f) {
                    hideActionButton()
                } else if(current_fragment != Fragments.SHOP){
                    showActionButton()
                }
            }
            override fun onPanelStateChanged(
                panel: View, previousState: PanelState, newState: PanelState ) {
            }
        })
    }

    private fun hideActionButton() {
        get_button.hide()
        cart_item_count.visibility = View.INVISIBLE
    }

    private fun showActionButton() {
        get_button.show()
        cart_item_count.visibility = View.VISIBLE
    }

    private fun hideSwipeUpContainer() {
        bottom_slide_up_container.panelState = PanelState.COLLAPSED
        bottom_slide_up_container.isClipPanel = true
        bottom_slide_up_container.isTouchEnabled = false
    }

    private fun showSwipeUpContainer() {
        bottom_slide_up_container.isTouchEnabled = true
    }
//    endregion
}