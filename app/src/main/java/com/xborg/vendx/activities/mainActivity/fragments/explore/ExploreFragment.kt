package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.MachineCardAdapter
import com.xborg.vendx.database.Machine
import com.xborg.vendx.preferences.SharedPreference
import kotlinx.android.synthetic.main.fragment_explore.*
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.SharedViewModelFactory


const val TAG: String = "Explore"

class ExploreFragment : Fragment(), MachineCardAdapter.OnMachineCardListener, OnMapReadyCallback {

    private lateinit var viewModel: ExploreViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private var googleMap: GoogleMap? = null
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_explore, container, false)
        setupMap(rootView, savedInstanceState)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireNotNull(this.activity).application

        val homeViewModelFactory = ExploreViewModelFactory(application)
        viewModel = ViewModelProvider(activity!!, homeViewModelFactory).get(ExploreViewModel::class.java)

        val sharedViewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(activity!!, sharedViewModelFactory).get(SharedViewModel::class.java)

        sharedViewModel.userLocationAccessed.observe(viewLifecycleOwner, Observer { accessed ->
            if(accessed) {
                viewModel.userLocation.value = sharedViewModel.userLastLocation.value
                updateUserLocationOnMap()
            } else {
                switchOffScanMode()
            }
        })

    }


    private fun switchOffScanMode() {
        sharedViewModel.getUserLocation.value = false
    }

    override fun onCardClicked(machineId: String) {
    }

    private fun setupMap(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
    }

    private fun updateUserLocationOnMap() {
        val location = viewModel.userLocation.value!!
        val googleCamera: CameraPosition = CameraPosition.builder()
            .target(LatLng(location.Latitude, location.Longitude))
            .zoom(15F)
            .bearing(0F)
            .tilt(0F)
            .build()
        googleMap!!.animateCamera(
            CameraUpdateFactory.newCameraPosition(googleCamera),
            1000,
            null
        )
        googleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(location.Latitude, location.Longitude))
                .title("You are here")
        )
    }

    private fun updateMachineMarkersOnMap() {
//        viewModel.machinesInZone.value!!.forEach { machine ->
//            val machineLocation = machine.Location
//            googleMap!!.addMarker(
//                MarkerOptions()
//                    .position(LatLng(machineLocation.Latitude, machineLocation.Longitude))
//                    .title(machine.Name)
//            )
//        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.i(TAG, "onMapReady")
        googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.clear()
        this.googleMap = googleMap
    }
}