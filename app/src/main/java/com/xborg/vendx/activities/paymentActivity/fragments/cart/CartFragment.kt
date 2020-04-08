package com.xborg.vendx.activities.paymentActivity.fragments.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.activities.paymentActivity.SharedViewModel
import com.xborg.vendx.adapters.ItemCartSlipAdapter
import kotlinx.android.synthetic.main.fragment_cart.*

private var TAG = "CartFragment"

class CartFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProvider(activity!!).get(SharedViewModel::class.java)
        viewModel = ViewModelProvider(activity!!).get(CartViewModel::class.java)

        viewModel.cartDao.getLiveCartItems().observe(viewLifecycleOwner, Observer { cart ->

            rv_cart.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ItemCartSlipAdapter(cart, context)
            }

        })

    }

    private fun observerSharedViewModel() {

    }
}
