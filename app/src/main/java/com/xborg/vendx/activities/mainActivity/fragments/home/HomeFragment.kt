package com.xborg.vendx.activities.mainActivity.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.FeedbackActivity
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.activities.mainActivity.SharedViewModelFactory
import com.xborg.vendx.adapters.ItemGroupAdapter
import kotlinx.android.synthetic.main.fragment_home.*

private var TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var adapter: ItemGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ItemGroupAdapter(context!!)
        rv_machine_items.adapter = adapter

        val application = requireNotNull(this.activity).application

        val homeViewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(activity!!, homeViewModelFactory).get(HomeViewModel::class.java)

        val sharedViewModelFactory = SharedViewModelFactory(application)
        sharedViewModel = ViewModelProvider(activity!!, sharedViewModelFactory).get(SharedViewModel::class.java)

        sharedViewModel.checkedUserLocationAccessed.observe(
            viewLifecycleOwner,
            Observer { checked ->
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

        continue_location_permission.setOnClickListener {
            sharedViewModel.getUserLocation.value = true
        }

        viewModel.userDao.get()!!.observe(viewLifecycleOwner, Observer { user ->
            if(user!= null) {
                viewModel.userInventory.value = user.Inventory
                viewModel.updateHomeInventoryGroups()
            }
        })

        viewModel.machineDao.get().observe(viewLifecycleOwner, Observer { machines ->
            if(machines != null) {
                if(machines.isNotEmpty()) {
                    Log.i(TAG, "machines : $machines")
                    HomeViewModel.selectedMachine.value = machines[0]
                    viewModel.updateHomeInventoryGroups()
                }
            }
        })

        viewModel.homeInventoryGroups.observe(viewLifecycleOwner, Observer { groups ->
            if(groups != null) {
                adapter.submitList(groups)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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