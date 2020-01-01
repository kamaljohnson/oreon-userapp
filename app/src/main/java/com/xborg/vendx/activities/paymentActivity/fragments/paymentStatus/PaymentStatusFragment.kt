package com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.activities.paymentActivity.PaymentActivity
import com.xborg.vendx.activities.paymentActivity.SharedViewModel
import com.xborg.vendx.bluetooth.BluetoothConnectionActivity
import com.xborg.vendx.database.PaymentState
import com.xborg.vendx.database.PaymentStatus
import kotlinx.android.synthetic.main.fragment_payment_status.*

const val TAG = "PaymentStatusFragment"

class PaymentStatusFragment : Fragment(){

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: PaymentStatusViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observerSharedViewModel()

        get_button.setOnClickListener {
            proceedToConnectionCreation()
        }
        retry_button.setOnClickListener {
            initPaymentRetry()
        }
        home_button.setOnClickListener {
            proceedToMainActivity()
        }
    }

    private fun observerSharedViewModel() {

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(PaymentStatusViewModel::class.java)

        sharedViewModel.paymentState.observe(this, Observer { updatedPaymentState ->
            if(viewModel.paymentState.value!! < updatedPaymentState) {
                viewModel.payment.value = sharedViewModel.payment.value
                viewModel.order.value = sharedViewModel.order.value
                viewModel.paymentState.value = updatedPaymentState
                when(updatedPaymentState) {
                    PaymentState.PaymentDone -> {
                        viewModel.sendPaymentToken()
                    }
                }
            }
        })

        viewModel.paymentState.observe(this, Observer { updatedPaymentState ->
            if(sharedViewModel.paymentState.value!! < updatedPaymentState) {
                sharedViewModel.payment.value = viewModel.payment.value
                sharedViewModel.order.value = viewModel.order.value
                sharedViewModel.paymentState.value = updatedPaymentState
                payment_status_text.text = viewModel.payment.value!!.status.toString()
                when (viewModel.payment.value!!.status) {
                    PaymentStatus.Successful -> onPaymentSuccessful()
                    PaymentStatus.Failed -> onPaymentFailed()
                }
            }
        })
    }

    private fun onPaymentSuccessful() {
        status_icon.setBackgroundResource(R.drawable.success)
        on_success_layout.visibility = View.VISIBLE
    }

    private fun onPaymentFailed() {
        status_icon.setBackgroundResource(R.drawable.error)
        on_failure_layout.visibility = View.VISIBLE
    }

    private fun initPaymentRetry() {
        viewModel.paymentState.value = PaymentState.PaymentRetry
    }

    private fun proceedToMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun proceedToConnectionCreation() {
        val intent = Intent(context, BluetoothConnectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
