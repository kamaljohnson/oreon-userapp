package com.xborg.vendx.activities.vendingActivity.fragments.status

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.activities.vendingActivity.SharedViewModel
import com.xborg.vendx.activities.vendingActivity.fragments.deviceCommunicator.DeviceConnectionStatus
import com.xborg.vendx.database.VendingState
import kotlinx.android.synthetic.main.fragment_vending_status.*

const val TAG = "VendingStatusFragment"
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

        sharedViewModel.vendState.observe(viewLifecycleOwner, Observer { updatedVendingState ->
            if (viewModel.vendState.value!! < updatedVendingState) {

                viewModel.vendState.value = updatedVendingState
                viewModel.bag.value = sharedViewModel.bag.value

                when (updatedVendingState) {
                    VendingState.DeviceConnected -> {
                        pairing_request_dialog.visibility = View.INVISIBLE
                        processing_gif.visibility = View.VISIBLE
                        object: CountDownTimer(10000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                Log.i(TAG, "Timer: tick")
                                //TODO: show a time_out counter
                            }
                            override fun onFinish() {
                                Log.i(TAG, "Timer: finish, Status: " + viewModel.vendState.value)
                                if(viewModel.vendState.value == VendingState.DeviceConnected ||
                                        viewModel.vendState.value == VendingState.Init) {
                                    displayRetry()
                                }
                            }
                        }.start()
                    }
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
        sharedViewModel.deviceConnectionStatus.observe(viewLifecycleOwner, Observer { connectionStatus ->
            Log.i(TAG, "here")
            when(connectionStatus) {
                DeviceConnectionStatus.ConnectionLost -> {
                    if(sharedViewModel.vendState.value != VendingState.VendingComplete) {
                        displayRetry()
                    }
                }
                DeviceConnectionStatus.ConnectionFailed -> {
                    if(sharedViewModel.vendState.value != VendingState.VendingComplete) {
                        displayRetry()
                    }
                }
            }
        })

        viewModel.vendState.observe(viewLifecycleOwner, Observer { updatedBagStatus ->
            if (sharedViewModel.vendState.value!! < updatedBagStatus) {

                sharedViewModel.vendState.value = updatedBagStatus
                sharedViewModel.bag.value = viewModel.bag.value
            }
        })

        viewModel.retryDeviceConnection.observe(viewLifecycleOwner, Observer { retry->
            sharedViewModel.retryDeviceConnection.value = retry
        })

        cancel_button.setOnClickListener {
            cancelVend()
        }

        retry_button.setOnClickListener {
            retryVend()
        }

        done_button.setOnClickListener {
            goToHome()
        }
    }

    private fun displayRetry() {
        processing_gif.visibility = View.INVISIBLE
        fail_gif.visibility = View.VISIBLE
        vending_fail_resolution_layout.visibility = View.VISIBLE
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

    private fun cancelVend() {
        goToHome()      //TODO: handle this using server code
    }

    private fun retryVend() {
        viewModel.vendState.value = VendingState.Init
        processing_gif.visibility = View.VISIBLE
        fail_gif.visibility = View.INVISIBLE
        vending_fail_resolution_layout.visibility = View.INVISIBLE
        viewModel.retryDeviceConnection.value = true
    }

    private fun goToHome() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}