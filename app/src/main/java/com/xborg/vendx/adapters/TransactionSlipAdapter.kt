package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.Transaction
import kotlinx.android.synthetic.main.transaction_slip.view.*

class TransactionSlipAdapter(
    val transactions: List<Transaction>,
    val context: Context
) : RecyclerView.Adapter<TransactionSlipAdapter.TransactionSlipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionSlipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_slip, parent, false)
        return TransactionSlipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size

    }

    override fun onBindViewHolder(holder: TransactionSlipViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.id.text = transaction.id
        holder.status.text = transaction.status.toString()
        holder.timeStamp.text = transaction.timeStamp
    }

    class TransactionSlipViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val id: TextView = view.transaction_id
        val status: TextView = view.status
        val timeStamp: TextView = view.time_stamp
        val transactionTypeImage = view.transaction_type_image

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //TODO: show transaction details
        }

    }
}
