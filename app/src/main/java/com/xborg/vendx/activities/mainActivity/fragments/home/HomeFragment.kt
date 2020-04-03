package com.xborg.vendx.activities.mainActivity.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.FeedbackActivity
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.activities.mainActivity.SharedViewModelFactory
import com.xborg.vendx.adapters.ItemCardAdapter
import com.xborg.vendx.database.ItemDetailDatabase
import com.xborg.vendx.database.UserDatabase
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
    }

    override fun onItemAddedToCart(itemId: String, paid: Boolean): Boolean {
        return sharedViewModel.addItemToCart(itemId, paid)
    }

    override fun onItemRemovedFromCart(itemId: String, paid: Boolean): Boolean {
        return sharedViewModel.removeItemFromCart(itemId, paid)
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