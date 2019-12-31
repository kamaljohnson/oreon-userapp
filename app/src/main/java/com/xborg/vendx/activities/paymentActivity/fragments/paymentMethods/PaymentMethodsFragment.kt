package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.SharedViewModel
import com.xborg.vendx.database.PaymentState
import com.xborg.vendx.database.PaymentStatus
import kotlinx.android.synthetic.main.fragment_payment_methods.*

private const val TAG = "PaymentMethodFragment"

class PaymentMethodsFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: PaymentMethodsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_methods, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observerSharedViewModel()

        get_button.setOnClickListener {
            get_button.isClickable = false
            progress_bar.visibility = View.VISIBLE
            //TODO: update local order with shared order
            viewModel.postOrderDetails()
        }
    }

    private fun observerSharedViewModel() {

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(PaymentMethodsViewModel::class.java)

        sharedViewModel.cartItem.observe(this, Observer { updatedCart ->
            viewModel.cartItems.value = updatedCart
        })
        sharedViewModel.machineItems.observe(this, Observer { updatedMachineItems ->
            Log.i(TAG, "machineItems updated: $updatedMachineItems")
        })
        sharedViewModel.shelfItems.observe(this, Observer { updatedShelfItems ->
            Log.i(TAG, "shelfItems updated: $updatedShelfItems")
        })

        sharedViewModel.paymentState.observe(this, Observer { updatedPaymentState ->
            if(viewModel.paymentState.value!! < updatedPaymentState) {
                viewModel.payment.value = sharedViewModel.payment.value
                viewModel.order.value = sharedViewModel.order.value
                when(updatedPaymentState) {
                    PaymentState.OrderInit -> {
                        viewModel.calculatePayableAmount()
                        total_amount_text.text = viewModel.order.value!!.amount.toString()
                    }
                }
                viewModel.paymentState.value = updatedPaymentState
            }
        })

        viewModel.paymentState.observe(this, Observer { updatedPaymentState ->
            if(sharedViewModel.paymentState.value!! < updatedPaymentState) {
                sharedViewModel.payment.value = viewModel.payment.value
                sharedViewModel.order.value = viewModel.order.value
                sharedViewModel.paymentState.value = updatedPaymentState
            }
        })
    }
}
