package com.xborg.vendx.activities.mainActivity.fragments.explore

import android.os.Bundle
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
import com.xborg.vendx.preferences.SharedPreference
import kotlinx.android.synthetic.main.fragment_explore.*

const val TAG: String = "Explore"

class ExploreFragment : Fragment(), MachineCardAdapter.OnMachineCardListener{

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

        viewModel.selectedMachine.observe(viewLifecycleOwner, Observer { selectedMachine->
            if(selectedMachine.Code == "Dummy") {
                selected_machine_code.text = "Explore?"
            } else {
                selected_machine_code.text = selectedMachine.Code
            }
            sharedViewModel.selectedMachine.value = selectedMachine
            setSelectedMachineMac(selectedMachine.Mac)
        })

        viewModel.machinesInZone.observe(viewLifecycleOwner, Observer {
            displayMachinesInZone()
        })

        viewModel.machineNearby.observe(viewLifecycleOwner, Observer {

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
