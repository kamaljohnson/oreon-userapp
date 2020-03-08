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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.MachineCardAdapter
import com.xborg.vendx.preferences.SharedPreference
import kotlinx.android.synthetic.main.fragment_explore.*

const val TAG: String = "Explore"

class ExploreFragment : Fragment(), MachineCardAdapter.OnMachineCardListener {

    private lateinit var viewModel: ExploreViewModel
    private lateinit var sharedViewModel: SharedViewModel

    lateinit var mapFragment: SupportMapFragment
    lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_explore, container, false)
        setupMapView()
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ExploreViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.userLocationAccessed.observe(viewLifecycleOwner, Observer { accessed ->
            if(accessed) {
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
            Log.i(TAG, "here nearby machines" + viewModel.machineNearby.value)
        })

        viewModel.selectedMachine.observe(viewLifecycleOwner, Observer { selectedMachine->
            Log.i(TAG, "here selected machine" + viewModel.machineNearby.value)
            if(selectedMachine.Code == "Dummy") {
                selected_machine_code.text = "Explore?"
            } else {
                selected_machine_code.text = selectedMachine.Code
            }
            sharedViewModel.selectedMachine.value = selectedMachine
            setSelectedMachineMac(selectedMachine.Mac)
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
        updateMapView()
    }

    private fun updateMachineCardRV() {
        rv_machine_cards.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MachineCardAdapter(viewModel.machinesInZone.value!!, context, this@ExploreFragment)
        }
    }

    private fun setupMapView() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            OnMapReadyCallback { googleMap ->
                googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                googleMap.clear()
                val googleCamera: CameraPosition = CameraPosition.builder()
                    .target(LatLng(37.4219999, -122.0862462))
                    .zoom(10F)
                    .bearing(0F)
                    .tilt(45F)
                    .build()
                googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(googleCamera),
                    10000,
                    null
                )
            }
        }
    }

    private fun updateMapView() {

    }

    override fun onCardClicked(machineId: String) {
        viewModel.changeSelectedMachine(machineId)
    }

    private fun setSelectedMachineMac(mac: String) {
        val preference = SharedPreference(context!!)
        preference.setSelectedMachineMac(mac)
    }
}
