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
import com.xborg.vendx.adapters.ItemGroupAdapter
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.ItemCardAdapter
import com.xborg.vendx.databinding.FragmentHomeBinding

private var TAG = "HomeFragment"

class HomeFragment : Fragment(), ItemCardAdapter.OnItemListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Home onCreate called!")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView called!")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(HomeViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.allGroupItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "allGroupItems updated")
            updateItemGroupToRV()
        })
        viewModel.machineItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "machineItemList Updated")

            sharedViewModel.setMachineItems(viewModel.machineItems.value!!)
        })
        viewModel.shelfItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "shelfItemList Updated")

            sharedViewModel.setShelfItems(viewModel.shelfItems.value!!)
        })
        sharedViewModel.cartItem.observe(viewLifecycleOwner, Observer { updatedCart ->
            Log.i(TAG, "cart updated : $updatedCart")

        })

    }

    private fun updateItemGroupToRV() {
        Log.i(
            TAG,
            "allGroupItems : ${viewModel.allGroupItems.value?.size} " + binding.rvMachineItems
        )

        binding.rvMachineItems.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ItemGroupAdapter(viewModel.allGroupItems.value!!, context, this@HomeFragment)
        }
    }

    override fun onItemAddedToCart(itemId: String, itemLoc: String) {
        Log.i(TAG, "item : $itemId from $itemLoc added to cart")
        sharedViewModel.addItemToCart(itemId, itemLoc)

    }

    override fun onItemRemovedFromCart(itemId: String, itemLoc: String) {
        Log.i(TAG, "item : $itemId from $itemLoc removed from cart")
        sharedViewModel.removeItemFromCart(itemId, itemLoc)
    }
}