package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.*

private var TAG = "RoomSlipAdapter"

class RoomSlipAdapter(
    val context: Context
) : ListAdapter<RoomSlip, RoomSlipAdapter.RoomSlipViewHolder>(RoomSlipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomSlipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.room_slip, parent, false)

        return RoomSlipViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomSlipViewHolder, position: Int) {
        val chatMessage = getItem(position)

    }

    class RoomSlipViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }

    }
}

class RoomSlipDiffCallback: DiffUtil.ItemCallback<RoomSlip>() {
    override fun areItemsTheSame(oldItem: RoomSlip, newItem: RoomSlip): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RoomSlip, newItem: RoomSlip): Boolean {
        return oldItem == newItem
    }
}

