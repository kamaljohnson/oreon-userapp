package com.xborg.vendx.activities.deviceConnectorActivity.fragments.selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.deviceConnectorActivity.SharedViewModel

class SelectorFragment : Fragment() {
    private lateinit var viewModel: SelectorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prerequisites, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(SelectorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.currentConnectionModePermissionRequirements.observe(this, Observer {
            sharedViewModel.currentConnectionModePermissionRequirements.value =
                viewModel.currentConnectionModePermissionRequirements.value!!

            sharedViewModel.jumpToNextStep()
        })
    }

}
