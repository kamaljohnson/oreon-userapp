package com.xborg.vendx.activities.paymentActivity.fragments.paymentMethods

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.SharedViewModel
import kotlinx.android.synthetic.main.fragment_payment_methods.*

private const val TAG = "PaymentMethodFragment"

@SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProvider(activity!!).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(activity!!).get(PaymentMethodsViewModel::class.java)

        viewModel.cartDao.getLiveCartItems().observe(viewLifecycleOwner, Observer { cart ->
            if(cart != null) {
                viewModel.cart.value = cart
                viewModel.calculatePaymentAmount()
            }
        })

        viewModel.paymentAmount.observe(viewLifecycleOwner, Observer { amount ->
            Log.i(TAG, "payment amount : $amount")
            total_amount_text.text = "$amount ₹"

            if(amount > 0) {
                updateUiForPayLogic()
            } else {
                updateUiForGetLogic()
            }

        })

    }

    private fun updateUiForGetLogic() {

        checkout_button.text = "GET"
        bill.visibility = View.GONE

    }


    private fun updateUiForPayLogic() {

        checkout_button.text = "PAY"
        get_info_text.visibility = View.GONE
    }

}
