package com.xborg.vendx.activities.mainActivity.fragments.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.activities.mainActivity.SharedViewModel
import com.xborg.vendx.adapters.TransactionSlipAdapter
import kotlinx.android.synthetic.main.fragment_history.*

private var TAG = "HistoryFragment"

class HistoryFragment : Fragment() {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Shelf onCreate called!")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history,container,false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        viewModel.transactions.observe(this, Observer { updatedTransactions ->
            progress_bar.visibility = View.GONE
            if(updatedTransactions.isNotEmpty()) {
                shelf_empty_container.visibility = View.GONE
                updateTransactionsToRV()
            } else {
                shelf_empty_container.visibility = View.VISIBLE
            }
        })
    }

    private fun updateTransactionsToRV() {
        rv_transactions.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TransactionSlipAdapter(viewModel.transactions.value!!, context)
        }
    }
}