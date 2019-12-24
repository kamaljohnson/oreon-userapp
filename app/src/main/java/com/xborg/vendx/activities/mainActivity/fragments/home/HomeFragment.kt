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
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.SupportClasses.ItemGroupAdapter
import com.xborg.vendx.databinding.FragmentHomeBinding
import com.xborg.vendx.models.ItemGroupModel

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate called!")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "onCreateView called!")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        MainActivity.items = viewModel.allMachineItems      //TODO: this code needed to be changed in the future
        viewModel.allGroupItems.observe(this, Observer { newAllGroupItems ->

            Log.i(TAG, "allGroupItems updated")
            updateItemGroupToRV(newAllGroupItems)
        })

        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    private fun updateItemGroupToRV(allGroupItems: ArrayList<ItemGroupModel>) {

        Log.i(TAG, "allGroupItems : $allGroupItems")
        binding.rvMachineItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvMachineItems.adapter = context?.let { ItemGroupAdapter(allGroupItems) }
    }
}