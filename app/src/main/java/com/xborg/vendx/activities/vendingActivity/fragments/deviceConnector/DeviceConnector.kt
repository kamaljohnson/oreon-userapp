package com.xborg.vendx.activities.vendingActivity.fragments.deviceConnector

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.database.Machine
import com.xborg.vendx.preferences.SharedPreference

const val TAG: String = "DeviceConnector"

class DeviceConnector : Fragment() {

    private lateinit var viewModel: DeviceConnectorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Device Connector Loaded")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_connector, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(DeviceConnectorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.selectedMachine.observe(viewLifecycleOwner, Observer { machine ->
            if(machine != null) {
                sharedViewModel.selectedMachine.value = machine
            }
        })

        sharedViewModel.selectedMachineNearby.observe(viewLifecycleOwner, Observer { isNearby ->
            viewModel.selectedMachineNearby.value = isNearby
            Log.i(TAG, "Selected Machine Is Nearby: $isNearby")
        })

        getSelectedMachineMac()
    }

    private fun getSelectedMachineMac() {
        val machine = Machine()
        val preference = SharedPreference(context!!)
        machine.Mac = preference.getSelectedMachineMac()!!
        viewModel.selectedMachine.value = machine
    }
}
