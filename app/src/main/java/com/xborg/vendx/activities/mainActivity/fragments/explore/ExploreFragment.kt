package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.MachineCardAdapter
import kotlinx.android.synthetic.main.fragment_explore.*

const val TAG: String = "Explore"

class ExploreFragment : Fragment() {

    private lateinit var viewModel: ExploreViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ExploreViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.userLocationAccessed.observe(this, Observer { accessed ->
            if(accessed) {
                Log.i(TAG, "here")
                scanForNearbyMachines()
            } else {
                switchOffScanMode()
            }
        })

        viewModel.machinesNearby.observe(this, Observer {
            displayNearbyMachines()
        })

        explore_button.setOnClickListener{
            sharedViewModel.getUserLocation.value = true
        }
    }

    private fun scanForNearbyMachines() {
        scan_mode_switch.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
        explore_button.isClickable = false

        viewModel.userLocation.value = sharedViewModel.userLastLocation.value
        sharedViewModel.getUserLocation.value = false

        viewModel.requestNearbyMachines()
    }

    private fun switchOffScanMode() {
        scan_mode_switch.visibility = View.VISIBLE
        scan_mode_switch.isChecked = false
        sharedViewModel.getUserLocation.value = false
    }

    private fun displayNearbyMachines() {
        progress_bar.visibility = View.GONE
        updateMachineCardRV()
        updateMapView()
    }

    private fun updateMachineCardRV() {
        rv_machine_cards.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MachineCardAdapter(viewModel.machinesNearby.value!!, context)
        }
    }
    private fun updateMapView() {

    }
}
