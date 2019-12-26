package com.xborg.vendx.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.databinding.ItemGroupHolderBinding

class ItemGroupAdapter: ListAdapter<ItemGroupModel, ItemGroupAdapter.GroupViewHolder>(GroupDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val parent = getItem(position)
        holder.bind(parent)
    }

    class GroupViewHolder(val binding: ItemGroupHolderBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(parent: ItemGroupModel) {
            binding.rvItemGroup.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = ItemAdapter()
                (adapter as ItemAdapter).submitList(parent.items)

                Log.i("TAG", "onBindViewHolder")

                if(!parent.draw_line_breaker) {
                    binding.lineBreaker.visibility = View.INVISIBLE
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): GroupViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupHolderBinding.inflate(layoutInflater, parent, false)
                return GroupViewHolder(binding)
            }
        }
    }
}

class GroupDiffCallback: DiffUtil.ItemCallback<ItemGroupModel>() {
    override fun areItemsTheSame(oldItem: ItemGroupModel, newItem: ItemGroupModel): Boolean {
        return oldItem.items == newItem.items
    }

    override fun areContentsTheSame(oldItem: ItemGroupModel, newItem: ItemGroupModel): Boolean {
        return oldItem == newItem
    }
}