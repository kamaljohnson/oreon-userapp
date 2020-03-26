package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

        viewModel = ViewModelProviders.of(activity!!).get(ExploreViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.userLocationAccessed.observe(viewLifecycleOwner, Observer { accessed ->
            if(accessed) {
                viewModel.userLocation.value = sharedViewModel.userLastLocation.value
                updateUserLocationOnMap()
                scanForMachinesInZone()
            } else {
                switchOffScanMode()
            }
        })

        sharedViewModel.apiCallRetry.observe(viewLifecycleOwner, Observer { retry ->
            if(retry) {
                if(sharedViewModel.userLocationAccessed.value!!){
                    scanForMachinesInZone()
                } else {
                    switchOffScanMode()
                }
            }
        })

        sharedViewModel.machineNearby.observe(viewLifecycleOwner, Observer { machines ->
            viewModel.machineNearby.value = machines
            viewModel.selectNearestMachineToUser()
            Log.i(TAG, "machines nearby" + viewModel.machineNearby.value)
        })

        viewModel.selectedMachine.observe(viewLifecycleOwner, Observer { selectedMachine->
            Log.i(TAG, "here selected machine" + viewModel.machineNearby.value)
            if(selectedMachine.Code == "Dummy") {
                selected_machine_code.text = "Explore?"
            } else {
                selected_machine_code.text = selectedMachine.Code
            }
            sharedViewModel.selectedMachine.value = selectedMachine
            setSelectedMachine(selectedMachine)
        })

        viewModel.machinesInZone.observe(viewLifecycleOwner, Observer { machines ->
            sharedViewModel.machinesInZone.value = machines
            displayMachinesInZone()
        })

        viewModel.apiCallError.observe(viewLifecycleOwner, Observer { error ->
            if(error) {
                sharedViewModel.apiCallError.value = error
            }
        })

        viewModel.debugText.observe(viewLifecycleOwner, Observer { text ->
            sharedViewModel.debugText.value += TAG + text
        })
    }

    private fun scanForMachinesInZone() {
        viewModel.debugText.value = "init explorer\n\n"

        progress_bar.visibility = View.VISIBLE
        selected_machine_code.isClickable = false

        viewModel.userLocation.value = sharedViewModel.userLastLocation.value
        sharedViewModel.getUserLocation.value = false

        viewModel.requestMachinesInZone()
    }

    private fun switchOffScanMode() {
        sharedViewModel.getUserLocation.value = false
    }

    private fun displayMachinesInZone() {
        progress_bar.visibility = View.GONE
        updateMachineCardRV()
        updateMachineMarkersOnMap()
    }

    private fun updateMachineCardRV() {
        rv_machine_cards.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MachineCardAdapter(viewModel.machinesInZone.value!!, context, this@ExploreFragment)
        }
    }

    override fun onCardClicked(machineId: String) {
        viewModel.changeSelectedMachine(machineId)
    }

    private fun setSelectedMachine(machine: Machine) {
        val preference = SharedPreference(context!!)
        preference.setSelectedMachine(machine)
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
        viewModel.machinesInZone.value!!.forEach { machine ->
            val machineLocation = machine.Location
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(machineLocation.Latitude, machineLocation.Longitude))
                    .title(machine.Code)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.i(TAG, "onMapReady")
        googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.clear()
        this.googleMap = googleMap
    }
}