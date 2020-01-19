package com.xborg.vendx.activities.vendingActivity.fragments.status

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.database.VendingState
import kotlinx.android.synthetic.main.fragment_vending_status.*

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
                    VendingState.VendDone -> {

                    }
                    VendingState.EncryptedDeviceLogReceivedFromDevice -> {
                        viewModel.sendEncryptedDeviceLogToServer()
                    }
                }
            }
            updateStatusUI()
        })

        viewModel.vendState.observe(this, Observer { updatedBagStatus ->
            if (sharedViewModel.vendState.value!! < updatedBagStatus) {

                sharedViewModel.vendState.value = updatedBagStatus
                sharedViewModel.bag.value = viewModel.bag.value
            }
        })

        done_button.setOnClickListener {
            goToHome()
        }
    }

    private fun updateStatusUI() {
        when(sharedViewModel.vendState.value) {
            VendingState.Init -> {

            }
            VendingState.DeviceConnected -> {
                Log.i(TAG, "connection progress set to Green")
                connection_progress.setBackgroundColor(Color.GREEN)
            }
            VendingState.EncryptedOtpReceivedFromDevice -> {

            }
            VendingState.EncryptedOtpPlusBagReceivedFromServer -> {

            }
            VendingState.VendProgress -> {

            }
            VendingState.VendDone -> {
                Log.i(TAG, "vending progress set to Green")
                vending_progress.setBackgroundColor(Color.GREEN)
            }
            VendingState.EncryptedDeviceLogReceivedFromDevice -> {

            }
            VendingState.EncryptedVendStatusReceivedFromServer -> {

            }
            VendingState.VendingComplete -> {
                Log.i(TAG, "finishing progress set to Green")
                processing_gif.visibility = View.INVISIBLE
                success_gif.visibility = View.VISIBLE
                finishing_progress.setBackgroundColor(Color.GREEN)
                vending_complete_layout.visibility = View.VISIBLE
            }
        }
    }

    private fun goToHome() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}