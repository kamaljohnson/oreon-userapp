package com.xborg.vendx.activities.mainActivity.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.ItemGroupAdapter
import com.xborg.vendx.activities.mainActivity.fragments.SharedViewModel
import com.xborg.vendx.databinding.FragmentHomeBinding

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Home onCreate called!")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView called!")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

//        MainActivity.items = viewModel.machineItems      //TODO: this code needed to be changed in the future

        viewModel.allGroupItems.observe(this, Observer {
            Log.i(TAG, "allGroupItems updated")
            updateItemGroupToRV()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.machineItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "machineItemList Updated")

            sharedViewModel.setMachineItems(viewModel.machineItems.value!!)
        })

        viewModel.shelfItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "shelfItemList Updated")

            sharedViewModel.setShelfItems(viewModel.shelfItems.value!!)
        })
    }

    private fun updateItemGroupToRV() {
        Log.i(TAG, "allGroupItems : ${viewModel.allGroupItems.value?.size} " + binding.rvMachineItems)

        binding.rvMachineItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMachineItems.adapter = ItemGroupAdapter(viewModel.allGroupItems.value ?: ArrayList())
    }
}