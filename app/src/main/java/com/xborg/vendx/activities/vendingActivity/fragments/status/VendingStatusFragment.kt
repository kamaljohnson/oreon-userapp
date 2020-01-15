package com.xborg.vendx.activities.vendingActivity.fragments.status

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.FeedbackActivity
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.database.VendingState

const val TAG = "ServerCommunicator"

class VendingStatusFragment : Fragment() {

    private lateinit var viewModel: VendingStatusViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vending_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(VendingStatusViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.vendState.observe(this, Observer { updatedVendingState ->
            if (viewModel.vendState.value!! < updatedVendingState) {

                viewModel.vendState.value = updatedVendingState
                viewModel.bag.value = sharedViewModel.bag.value

                when (updatedVendingState) {
                    VendingState.EncryptedOtpReceivedFromDevice -> {
                        viewModel.sendEncryptedOtpToServer()
                    }
                    VendingState.VendingDone -> {

                    }
                    VendingState.EncryptedDeviceLogReceivedFromDevice -> {
                        viewModel.sendEncryptedDeviceLogToServer()
                    }
                }
            }
        })

        sharedViewModel.currentVendingCount.observe(this, Observer { updatedCurrentVendingCount ->
            updateVendingCount(updatedCurrentVendingCount)
        })

        viewModel.vendState.observe(this, Observer { updatedBagStatus ->
            if (sharedViewModel.vendState.value!! < updatedBagStatus) {

                sharedViewModel.vendState.value = updatedBagStatus
                sharedViewModel.bag.value = viewModel.bag.value
            }
        })
    }

    private fun updateVendingCount(count : Int) {  //TODO: display vending progress as item vended
        Log.i(TAG, "vending count : $count")
    }
}
