package com.xborg.vendx.activities.vendingActivity.fragments.communicators.server

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.database.BagStatus

const val TAG = "ServerCommunicator"

class ServerCommunicatorFragment : Fragment() {

    private lateinit var viewModel: ServerCommunicatorViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_server_communicator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ServerCommunicatorViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.bagStatus.observe(this, Observer { updatedBagStatus ->
            if (viewModel.bagStatus.value!! < updatedBagStatus) {

                viewModel.bagStatus.value = updatedBagStatus
                viewModel.bag.value = sharedViewModel.bag.value

                when (updatedBagStatus) {
                    BagStatus.EncryptedOtpReceived -> {
                        viewModel.sendEncryptedOtp()
                    }
                }
            }
        })

        viewModel.bagStatus.observe(this, Observer { updatedBagStatus ->
            if (sharedViewModel.bagStatus.value!! < updatedBagStatus) {

                sharedViewModel.bagStatus.value = updatedBagStatus
                sharedViewModel.bag.value = viewModel.bag.value

            }
        })
    }
}