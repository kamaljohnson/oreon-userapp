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
import kotlinx.android.synthetic.main.fragment_payment_methods.*

const val TAG = "PaymentMethodFragment"

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

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(PaymentMethodsViewModel::class.java)

        observerSharedViewModel()

        get_button.setOnClickListener {
            get_button.isClickable = false
            progress_bar.visibility = View.VISIBLE
            viewModel.order.value = sharedViewModel.order.value
            viewModel.postOrderDetails()
        }
    }

    private fun observerSharedViewModel() {

        sharedViewModel.cartItem.observe(this, Observer { updatedCart ->
            Log.i(TAG, "cartItems updated: $updatedCart")

            viewModel.cartItems.value = updatedCart
            viewModel.calculatePayableAmount()
        })
        sharedViewModel.machineItems.observe(this, Observer { updatedMachineItems ->
            Log.i(TAG, "machineItems updated: $updatedMachineItems")
        })
        sharedViewModel.shelfItems.observe(this, Observer { updatedShelfItems ->
            Log.i(TAG, "shelfItems updated: $updatedShelfItems")
        })

        viewModel.payableAmount.observe(this, Observer { updatedPayableAmount ->
            total_amount_text.text = "$updatedPayableAmount Rs"
        })
        viewModel.order.observe(this, Observer { updatedOrder ->
            if(updatedOrder.id != "") {
                sharedViewModel.order.value = updatedOrder
                sharedViewModel.payableAmount.value = viewModel.payableAmount.value
                sharedViewModel.paymentInitiated.value = true
                progress_bar.visibility = View.GONE
            }
        })

    }
}
