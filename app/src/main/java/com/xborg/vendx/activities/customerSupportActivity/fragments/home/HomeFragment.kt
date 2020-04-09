package com.xborg.vendx.activities.customerSupportActivity.fragments.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.R
import com.xborg.vendx.adapters.RoomSlipAdapter
import com.xborg.vendx.database.RoomSlip
import kotlinx.android.synthetic.main.fragment_customer_support_home.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_customer_support_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(activity!!).get(HomeViewModel::class.java)

        val _adapter = RoomSlipAdapter(context!!)

        rv_rooms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
        }

        viewModel.rooms.observe(viewLifecycleOwner, Observer { rooms ->
            if(rooms != null) {
                _adapter.submitList(rooms)
            }
        })
    }

}
