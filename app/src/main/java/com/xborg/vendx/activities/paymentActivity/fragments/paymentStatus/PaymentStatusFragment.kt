package com.xborg.vendx.activities.paymentActivity.fragments.paymentStatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.SharedViewModel
import kotlinx.android.synthetic.main.fragment_payment_status.*

const val TAG = "PaymentStatusFragment"

class PaymentStatusFragment : Fragment(){
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)
        observerSharedViewModel()
    }

    private fun observerSharedViewModel() {
        sharedViewModel.paymentStatus.observe(this, Observer { status ->
            payment_status_container.text = status.toString()
        })
    }
}
