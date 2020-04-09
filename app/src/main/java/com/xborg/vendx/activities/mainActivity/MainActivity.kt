package com.xborg.vendx.activities.mainActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.fragments.explore.ExploreFragment
import com.xborg.vendx.activities.mainActivity.fragments.history.HistoryFragment
import com.xborg.vendx.activities.mainActivity.fragments.home.HomeFragment
import com.xborg.vendx.activities.mainActivity.fragments.shop.ShopFragment
import com.xborg.vendx.activities.paymentActivity.PaymentActivity
import com.xborg.vendx.database.machine.Machine
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

private const val REQUEST_ENABLE_BT = 2
private const val REQUEST_ENABLE_LOC = 3

var TAG = "MainActivity"

private var mLayout: SlidingUpPanelLayout? = null
private var retryDialogDisplayed = false

enum class Fragments {
    HOME,
    SHOP,
    HISTORY
}

class MainActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var intentFilter: IntentFilter
    private lateinit var broadcastReceiver: BroadcastReceiver

    companion object {
        var current_fragment  = MutableLiveData<Fragments>(Fragments.HOME)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        debug_text_view.movementMethod = ScrollingMovementMethod()

        val application = requireNotNull(this).application

        val viewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(this, viewModelFactory).get(SharedViewModel::class.java)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sharedViewModel.getUserLocation.observe(this, Observer { scan ->
            if (scan) {
                getCurrentLocation()
            }
        })

        sharedViewModel.userLocationAccessed.observe(this, Observer { accessed ->
            if(accessed && current_fragment.value == Fragments.HOME) {
                showSwipeUpContainer()
            }
        })

        current_fragment.observe(this, Observer { fragment ->
            if(fragment == Fragments.HOME) {
                if(sharedViewModel.userLocationAccessed.value == true) {
                    showSwipeUpContainer()
                }
            }
        })

        sharedViewModel.isInternetAvailable.observe(this, Observer { available ->
            if(!available) {
                showInternetNotAvailableError()
            }
        })

        sharedViewModel.cartDao.getLiveCartItems().observe(this, Observer { cart ->
            if(cart!= null) {
               sharedViewModel.cart.value = cart
                Log.i(TAG, "Cart: $cart")
                sharedViewModel.processCart()

                cart_item_count.text = cart.size.toString()

                if(cart.isEmpty()) {
                    hideGetButton()
                } else {
                    showGetButton()
                }
            }
        })

        sharedViewModel.itemDetailDao.get().observe(this, Observer { items ->
            if(items.isNotEmpty()) {

            }
        })

        initBottomNavigationView()
        initBottomSwipeUpView()
        enableBluetooth()

        checkout_button.setOnClickListener {
            // TODO: use navigation graphs instead
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        alert_message_done_button.setOnClickListener {
            alert_message_layout.visibility = View.GONE
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

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        unregisterReceiver(broadcastReceiver)
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
                            Latitude = location.latitude,
                            Longitude = location.longitude
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

    private fun scanForNearbyMachines() {
        val listOfMachinesNearBy: ArrayList<Machine> = ArrayList()
        intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        broadcastReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent!!.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                       val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                       var machine = Machine()
//                       val machine = sharedViewModel.machinesInZone.value!!.find{ it.Mac.toUpperCase() == device.address.toUpperCase() }
                       if(machine != null) {
//                           if(listOfMachinesNearBy.find { it.Mac.toUpperCase() == machine.Mac.toUpperCase()} == null) {
//                               Log.i(TAG, "found added : " + device.address)
//                               listOfMachinesNearBy.add(machine)
//                               sharedViewModel.machineNearby.value = listOfMachinesNearBy
//                               Log.i(TAG, "listOfMachinesNearBy : $listOfMachinesNearBy")
//                           } else {
//                               //already added to list
//                           }
                       } else {
                           Log.i(TAG, "other bluetooth device")
                       }
                    }
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter.isDiscovering) {
            // Bluetooth is already in mode discovery mode, we cancel to restart it again
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
        Timer("discovery timer", false).schedule(10000) {
            Log.i(TAG, "discovery finished")
            bluetoothAdapter.cancelDiscovery()
            if(listOfMachinesNearBy.isEmpty()) {
//                sharedViewModel.machineNearby.postValue(ArrayList())
            }
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
                Latitude = location.latitude,
                Longitude = location.longitude
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

        changeFragment(HomeFragment(), "Home")

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    current_fragment.value = Fragments.HOME
                    changeFragment(HomeFragment(), "Home")
                    showGetButton()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_shop -> {
                    current_fragment.value = Fragments.SHOP
                    changeFragment(ShopFragment(), "Shop")
                    hideGetButton()
                    hideSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_history -> {
                    current_fragment.value = Fragments.HISTORY
                    changeFragment(HistoryFragment(), "History")
                    hideGetButton()
                    hideSwipeUpContainer()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun changeFragment(fragment: Fragment, title: String)  {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val currentFragment: Fragment? = fragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment)
        }

        var tempFragment: Fragment? = fragmentManager.findFragmentByTag(title)
        if (tempFragment == null) {
            tempFragment = fragment
            fragmentTransaction.add(fragment_container.id, tempFragment, title)
        } else {
            fragmentTransaction.show(tempFragment)
        }

        fragmentTransaction.setPrimaryNavigationFragment(tempFragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitAllowingStateLoss()
        supportActionBar!!.title = title
    }

    private fun initBottomSwipeUpView() {
        loadBottomSwipeUpFragment()

        mLayout = findViewById(bottom_slide_up_container.id)
        mLayout!!.coveredFadeColor = Color.TRANSPARENT
        mLayout!!.anchorPoint = 0.3f

        mLayout!!.setFadeOnClickListener {
            mLayout!!.panelState = PanelState.COLLAPSED
        }

        mLayout!!.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset > 0.05f) {
                    hideGetButton()
                } else if (current_fragment.value == Fragments.HOME) {
                    showGetButton()
                }
            }

            override fun onPanelStateChanged(
                panel: View, previousState: PanelState, newState: PanelState
            ) {

            }
        })

        showSwipeUpContainer()
    }

    private fun loadBottomSwipeUpFragment() {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(
            R.id.explore_fragment_container,
            ExploreFragment(),
            "ExploreFragment"
        )
        fragmentTransaction.commitAllowingStateLoss()
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

            if(sharedViewModel.cart.value!!.isNotEmpty()) {

                showRemoveCartMessage()

            } else {

                super.onBackPressed()
            }

        }
    }

    private fun showRemoveCartMessage() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Warning")
        builder.setMessage("Do you want to clear your cart?")
        builder.setNegativeButton("Yes") { _, _ ->
            sharedViewModel.resetCart()
        }
        builder.setPositiveButton("No") { _, _ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showInternetNotAvailableError() {
        Log.i(TAG, "API Call error occurred")
        jumpable_alert_message_layout.visibility = View.VISIBLE
        no_internet_connection_image.visibility = View.VISIBLE
        update_image_icon.visibility = View.INVISIBLE
        jumpable_alert_message_text.text = "Oops! it seams that you are not\nconnected to the internet\n\nPlease connect to the internet\nand Restart the application"
    }

    private fun showVersionDeprecatedError() {
        jumpable_alert_message_layout.visibility = View.VISIBLE
        update_image_icon.visibility = View.VISIBLE
        no_internet_connection_image.visibility = View.INVISIBLE
        jumpable_alert_message_text.text = "Please update the application\n\na new version is available in the\nplay store"
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
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

    private fun showConnectionErrorDialog() {
        if(!retryDialogDisplayed) {
            retryDialogDisplayed = true
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Network Error")
            builder.setMessage("An error occurred while connecting to server, please check your internet connection and retry")
            builder.setPositiveButton("Retry"){ _, _ ->
                retryDialogDisplayed = false
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
    //    endregion
}
