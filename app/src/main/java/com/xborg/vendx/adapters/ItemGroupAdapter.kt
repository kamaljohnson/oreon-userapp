package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.HomeItemGroup
import kotlinx.android.synthetic.main.item_group_holder.view.*

class ItemGroupAdapter(
    val context: Context,
    private val onItemListener: ItemCardAdapter.OnItemListener
) : ListAdapter<HomeItemGroup, ItemGroupAdapter.GroupViewHolder>(ItemGroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group_holder, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val parent = getItem(position)
        holder.title.text = parent.Title
        if(parent.Message != "") {
            holder.message.visibility = View.VISIBLE
            holder.message.text = parent.Message
        } else {
            val _adapter = ItemCardAdapter(parent.PaidInventory, context, onItemListener)
            _adapter.submitList(parent.Inventory)
            holder.groupItemsRV.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = _adapter
            }
        }
        holder.progressBar.visibility = if(parent.Inventory.isEmpty() && parent.Message != ""){
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupItemsRV: RecyclerView = view.rv_item_group
        val title: TextView = view.title
        val message: TextView = view.message_text
        val progressBar: ProgressBar = view.progress_bar

        val context: Context = itemView.context
    }

    interface OnItemListener {
        fun onRefreshRequest(itemId: String, itemLoc: String): Boolean
    }
}

class ItemGroupDiffCallback: DiffUtil.ItemCallback<HomeItemGroup>() {
    override fun areItemsTheSame(oldItem: HomeItemGroup, newItem: HomeItemGroup): Boolean {
        return oldItem.Title == newItem.Title
    }

    override fun areContentsTheSame(oldItem: HomeItemGroup, newItem: HomeItemGroup): Boolean {
        return oldItem == newItem
    }
}
