package com.xborg.vendx.activities.mainActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeFragment
import com.xborg.vendx.activities.mainActivity.fragments.history.HistoryFragment
import com.xborg.vendx.activities.mainActivity.fragments.shop.ShopFragment
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.fragments.explore.ExploreFragment
import com.xborg.vendx.activities.paymentActivity.PaymentActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_ENABLE_BT = 2
private const val REQUEST_ENABLE_LOC = 3

var TAG = "MainActivity"

private var mLayout: SlidingUpPanelLayout? = null

enum class Fragments {
    HOME,
    SHOP,
    SHELF
}

class MainActivity : FragmentActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        var current_fragment: Fragments =
            Fragments.HOME
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sharedViewModel.machineItems.observe(this, Observer {
            Log.i(TAG, "machineItem has begin changed")
        })
        sharedViewModel.shelfItems.observe(this, Observer {
            Log.i(TAG, "shelfItems has begin changed")
        })
        sharedViewModel.taggedCartItem.observe(this, Observer { updatedCart ->
            Log.i(TAG, "CartFragment updated: $updatedCart")

            var cartItemCount = 0
            updatedCart.forEach { item ->
                var itemCount = item.value
                cartItemCount += itemCount
            }

            cart_item_count.text = cartItemCount.toString()

            if (cartItemCount > 0) {
                showGetButton()
            } else {
                hideGetButton()
            }
        })
        sharedViewModel.getUserLocation.observe(this, Observer { scan ->
            if (scan) {
                getCurrentLocation()
            }
        })
        sharedViewModel.isInternetAvailable.observe(this, Observer { availability ->
            Log.i(TAG, "internet connection available: $availability")
        })

        sharedViewModel.userLocationAccessed.observe(this, Observer { accessed ->
            if(accessed && current_fragment == Fragments.HOME) {
                showSwipeUpContainer()
            } else {
                hideSwipeUpContainer()
            }
        })

        initBottomNavigationView()
        initBottomSwipeUpView()
        enableBluetooth()

        getCurrentLocation()

        checkout_button.setOnClickListener {
            // TODO: use navigation graphs instead
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("cartItems", sharedViewModel.getCartItemsAsPassable())
            intent.putExtra("machineItems", sharedViewModel.getMachineItemsAsJson())
            intent.putExtra("shelfItems", sharedViewModel.getShelfItemsAsJson())
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        sharedViewModel.isInternetAvailable.value = isInternetAvailable(this)

        if (sharedViewModel.getUserLocation.value == true &&
            sharedViewModel.userLocationAccessed.value == false
        ) {
            Log.i(TAG, "getLocation called from onResume")
            getCurrentLocation()
        }
    }

    // region Location and Bluetooth
    private fun enableBluetooth() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(
                "Your device does not support bluetooth," +
                        "please check with your local dealer and " +
                        "try again"
            )
                .setPositiveButton(R.string.Ok) { _, _ ->

                }
            builder.create()
            builder.show()
        } else if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            //bluetooth is already switched on
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            sharedViewModel.bluetoothPermission.value = PermissionStatus.Denied
            Log.e(TAG, "bluetooth permission not granted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.BLUETOOTH),
                    REQUEST_ENABLE_BT
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            sharedViewModel.bluetoothPermission.value = PermissionStatus.Granted
            Log.e(TAG, "bluetooth permission already granted")
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            sharedViewModel.locationPermission.value = PermissionStatus.Denied
            return false
        } else {
            sharedViewModel.locationPermission.value = PermissionStatus.Granted
            return true
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ENABLE_LOC
            )
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ENABLE_LOC
            )
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sharedViewModel.locationEnabled.value =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        return sharedViewModel.locationEnabled.value!!
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        sharedViewModel.userLastLocation.value = com.xborg.vendx.database.Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        sharedViewModel.getUserLocation.value = false
                        sharedViewModel.userLocationAccessed.value = true
                        sharedViewModel.checkedUserLocationAccessed.value = true
                    }
                }
            } else {
                if (sharedViewModel.getUserLocation.value!!) {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                sharedViewModel.checkedUserLocationAccessed.value = true
            }
        } else {
            if (sharedViewModel.getUserLocation.value!!) {
                requestLocationPermission()
            }
            sharedViewModel.checkedUserLocationAccessed.value = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.lastLocation
            sharedViewModel.userLastLocation.value = com.xborg.vendx.database.Location(
                latitude = location.latitude,
                longitude = location.longitude
            )
            sharedViewModel.userLocationAccessed.value = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                sharedViewModel.bluetoothPermission.value =
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        PermissionStatus.Granted
                    } else {
                        PermissionStatus.Denied
                    }
                return
            }
            REQUEST_ENABLE_LOC -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sharedViewModel.locationPermission.value = PermissionStatus.Granted
                    getCurrentLocation()
                } else {
                    sharedViewModel.locationPermission.value = PermissionStatus.Denied
                    sharedViewModel.userLocationAccessed.value = false
                }
                return
            }
        }
    }
//    endregion
//    region Activity Support functions

    private fun initBottomNavigationView() {

        val bottomNavigation = findViewById<BottomNavigationViewEx>(bottom_navigation.id)
        bottomNavigation.enableAnimation(false)
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.setTextVisibility(false)

        changeFragment(HomeFragment(), "HomeFragment")

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    current_fragment =
                        Fragments.HOME
                    changeFragment(HomeFragment(), "HomeFragment")
                    showGetButton()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_shop -> {
                    current_fragment =
                        Fragments.SHOP
                    changeFragment(ShopFragment(), "ShopFragment")
                    hideGetButton()
                    hideSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_history -> {
                    current_fragment =
                        Fragments.SHELF
                    changeFragment(HistoryFragment(), "HistoryFragment")
                    hideGetButton()
                    hideSwipeUpContainer()
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
        if (tempFragment == null) {
            tempFragment = fragment
            fragmentTransaction.add(fragment_container.id, tempFragment, tagFragmentName)
        } else {
            fragmentTransaction.show(tempFragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(tempFragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    private fun initBottomSwipeUpView() {
        loadBottomSwipeUpFragment()

        mLayout = findViewById(bottom_slide_up_container.id)
        mLayout!!.anchorPoint = 0.15f
        mLayout!!.coveredFadeColor = Color.WHITE

        mLayout!!.setFadeOnClickListener {
            mLayout!!.panelState = PanelState.COLLAPSED
        }

        mLayout!!.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset > 0.05f) {
                    hideGetButton()
                } else if (current_fragment != Fragments.SHOP) {
                    if (sharedViewModel.taggedCartItem.value!!.isNotEmpty()) {
                        showGetButton()
                    }
                }
            }

            override fun onPanelStateChanged(
                panel: View, previousState: PanelState, newState: PanelState
            ) {

            }
        })
    }

    private fun loadBottomSwipeUpFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.explore_fragment_container,
            ExploreFragment(),
            "ExploreFragment"
        )
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    private fun hideGetButton() {
        checkout_button.hide()
        cart_item_count.visibility = View.INVISIBLE
    }

    private fun showGetButton() {
        if (cart_item_count.text != "0") {
            checkout_button.show()
            cart_item_count.visibility = View.VISIBLE
        }
    }

    private fun hideSwipeUpContainer() {
        bottom_slide_up_container.panelState = PanelState.HIDDEN
    }

    private fun showSwipeUpContainer() {
        bottom_slide_up_container.panelState = PanelState.COLLAPSED
    }

    override fun onBackPressed() {
        if (mLayout != null &&
            (mLayout!!.panelState == PanelState.EXPANDED || mLayout!!.panelState == PanelState.ANCHORED)
        ) {
            mLayout!!.panelState = PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info)
                    if (i.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
        }
        return false
    }

//    endregion
}