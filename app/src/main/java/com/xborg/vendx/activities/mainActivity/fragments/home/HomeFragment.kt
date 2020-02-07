package com.xborg.vendx.activities.mainActivity.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.FeedbackActivity
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.ItemCardAdapter
import com.xborg.vendx.adapters.ItemGroupAdapter
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "HomeFragment"

class HomeFragment : Fragment(), ItemCardAdapter.OnItemListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Home onCreate called!")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView called!")

        return inflater.inflate(R.layout.fragment_home, container, false)
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
        viewModel.inventoryItems.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "inventoryItemList Updated")

            sharedViewModel.setInventoryItems(viewModel.inventoryItems.value!!)
        })
        viewModel.selectedMachineLoaded.observe(viewLifecycleOwner, Observer {  loaded ->
            sharedViewModel.selectedMachineLoaded.value = loaded
        })

        sharedViewModel.taggedCartItem.observe(viewLifecycleOwner, Observer { updatedCart ->
            Log.i(TAG, "CartFragment updated : $updatedCart")

        })
        sharedViewModel.selectedMachine.observe(viewLifecycleOwner, Observer { selectedMachine ->
            viewModel.selectedMachine.value = selectedMachine
            viewModel.changedSelectedMachine()
            //TODO: display loading sign in group-holder(Machine)
        })
        sharedViewModel.checkedUserLocationAccessed.observe(viewLifecycleOwner, Observer { checked ->
            if (checked) {
                if (sharedViewModel.userLocationAccessed.value == false) {
                    location_permission_access_dialog.visibility = View.VISIBLE
                } else {
                    location_permission_access_dialog.visibility = View.GONE
                }
            } else {
                location_permission_access_dialog.visibility = View.GONE
            }
        })
        sharedViewModel.userLocationAccessed.observe(viewLifecycleOwner, Observer { accessed ->
            if (accessed) {
                location_permission_access_dialog.visibility = View.GONE
            }
        })
        sharedViewModel.apiCallRetry.observe(viewLifecycleOwner, Observer { retry ->
            if(retry) {
                viewModel.handleInventoryUpdates()
            }
        })
        viewModel.apiCallError.observe(viewLifecycleOwner, Observer { error ->
            if(error) {
                sharedViewModel.apiCallError.value = error
            }
        })

        viewModel.debugText.observe(viewLifecycleOwner, Observer { text ->
            sharedViewModel.debugText.value += TAG + text
        })

        continue_location_permission.setOnClickListener {
            viewModel.debugText.value = "continue location permission\n\n"
            sharedViewModel.getUserLocation.value = true
        }
    }

    private fun updateItemGroupToRV() {
        Log.i(
            TAG,
            "allGroupItems : ${viewModel.allGroupItems.value?.size} " + rv_machine_items
        )

        rv_machine_items.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ItemGroupAdapter(viewModel.allGroupItems.value!!, context, this@HomeFragment)
        }
        progress_bar.visibility = View.GONE
    }

    override fun onItemAddedToCart(itemId: String, itemLoc: String): Boolean {
        Log.i(TAG, "item : $itemId from $itemLoc added to CartFragment")
        return sharedViewModel.addItemToCart(itemId, itemLoc)
    }

    override fun onItemRemovedFromCart(itemId: String, itemLoc: String): Boolean {
        Log.i(TAG, "item : $itemId from $itemLoc removed from CartFragment")
        return sharedViewModel.removeItemFromCart(itemId, itemLoc)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.feedback -> {
                showFeedback()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFeedback() {
        val intent = Intent(context, FeedbackActivity::class.java)
        startActivity(intent)
    }
}