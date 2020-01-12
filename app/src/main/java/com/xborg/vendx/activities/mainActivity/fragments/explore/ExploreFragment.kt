package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.PermissionStatus
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import kotlinx.android.synthetic.main.fragment_explore.*

val TAG: String = "Explore"

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

        sharedViewModel.checkLocationPermission.value = true

        sharedViewModel.locationPermission.observe(this, Observer { permissionStatus ->
            when(permissionStatus) {
                PermissionStatus.Granted -> {
                    scanOnScan()
                }
                PermissionStatus.Denied -> {
                    switchOffScan()
                }
            }
        })

        scan_mode_switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                sharedViewModel.requestLocationPermission.value = true
            }
        }
    }

    private fun scanOnScan() {
        scan_mode_switch.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
        viewModel.requestNearbyMachines()
    }

    private fun switchOffScan() {
        scan_mode_switch.visibility = View.VISIBLE
        scan_mode_switch.isChecked = false
    }

    private fun displayExplorer() {
        progress_bar.visibility = View.GONE
    }
}
