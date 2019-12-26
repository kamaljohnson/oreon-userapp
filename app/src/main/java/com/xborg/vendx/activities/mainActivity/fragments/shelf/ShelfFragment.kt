package com.xborg.vendx.activities.mainActivity.fragments.shelf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.adapters.ItemGroupAdapter
import com.xborg.vendx.activities.mainActivity.fragments.SharedViewModel
import kotlinx.android.synthetic.main.fragment_shelf.*

private var TAG = "ShelfFragment"

class ShelfFragment : Fragment() {

    private lateinit var viewModel: ShelfViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Shelf onCreate called!")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProviders.of(this).get(ShelfViewModel::class.java)

        viewModel.allGroupItems.observe(this, Observer {
            Log.i(TAG, "Shelf allGroupItems updated")

            updateItemGroupToRV()
        })

        return inflater.inflate(R.layout.fragment_shelf,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.machineItems.observe(viewLifecycleOwner, Observer {updatedMachineItemsList->
            Log.i(TAG, "machineItemList Updated")

            viewModel.shelfItems = updatedMachineItemsList
            viewModel.updateItemGroupModel()
        })

        sharedViewModel.shelfItems.observe(viewLifecycleOwner, Observer {updatedShelfItemList ->
            Log.i(TAG, "shelfItemList Updated")

            viewModel.shelfItems = updatedShelfItemList
            viewModel.updateItemGroupModel()
        })
    }

    private fun updateItemGroupToRV() {
        Log.i(TAG, "allGroupItems : ${viewModel.allGroupItems.value?.size} " + rv_shelf_items)

        rv_shelf_items.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_shelf_items.adapter = ItemGroupAdapter(viewModel.allGroupItems.value ?: ArrayList())
        shelf_empty_container.visibility = View.GONE
    }

}