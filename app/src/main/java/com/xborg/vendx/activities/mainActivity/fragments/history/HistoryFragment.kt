package com.xborg.vendx.activities.mainActivity.fragments.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.FeedbackActivity
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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        sharedViewModel.apiCallRetry.observe(viewLifecycleOwner, Observer { retry ->
            if(retry) {
                viewModel.getTransactions()
            }
        })

        viewModel.transactions.observe(viewLifecycleOwner, Observer { updatedTransactions ->
            progress_bar.visibility = View.GONE
            if(updatedTransactions.isNotEmpty()) {
                inventory_empty_container.visibility = View.GONE
                updateTransactionsToRV()
            } else {
                inventory_empty_container.visibility = View.VISIBLE
            }
        })

        viewModel.apiCallError.observe(viewLifecycleOwner, Observer { error ->
            if(error) {
                sharedViewModel.apiCallError.value = error
            }
        })

        rv_transactions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    //TODO: load next transactions from server
                }
            }
        })
    }

    private fun updateTransactionsToRV() {
        rv_transactions.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TransactionSlipAdapter(viewModel.transactions.value!!, context)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.feedback -> {
                showFeedback()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFeedback() {
        val intent = Intent(context, FeedbackActivity::class.java)
        startActivity(intent)
    }
}

