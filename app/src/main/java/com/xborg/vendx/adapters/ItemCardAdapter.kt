package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import com.xborg.vendx.databinding.ItemCardBinding

private var TAG = "ItemCardAdapter"

class ItemAdapter: ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ItemViewHolder private constructor(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        private val context: Context = itemView.context

        init {
            binding.purchaseCount.visibility = View.INVISIBLE
            binding.removeButton.visibility = View.INVISIBLE
        }

        fun bind(item: Item) {
            binding.itemId.text = item.id
            binding.name.text = item.name

            if (!item.inMachine) {
                binding.cardTop.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shop_basket_icon
                    )
                )
                binding.cardTop.isClickable = false
            }

            if (item.inShelf) {
                binding.cost.text = "-1"
                binding.cost.visibility = View.INVISIBLE
                binding.itemLimit.text =item.remainingInShelf.toString()
                binding.itemLimit.visibility = View.VISIBLE

                if (MainActivity.cart_items_from_shelf[item.id] != null) {
                    binding.purchaseCount.text =
                        MainActivity.cart_items_from_shelf[item.id].toString()
                    if (binding.purchaseCount.text.toString() != "0") {
                        binding.purchaseCount.visibility = View.VISIBLE
                    }
                }

            } else {
                binding.cost.text = "₹ ${item.cost}"
                binding.itemLimit.text = "10"
                binding.itemLimit.visibility = View.INVISIBLE

                if (MainActivity.cart_items[item.id] != null) {
                    binding.purchaseCount.text = MainActivity.cart_items[item.id].toString()
                    if (binding.purchaseCount.text.toString() != "0") {
                        binding.purchaseCount.visibility = View.VISIBLE
                    }
                }
            }

            binding.image.isClickable = item.inMachine

            Glide
                .with(context)
                .load(item.imgScrUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.blank_image)
                        .error(R.drawable.broken_image)
                )
                .into(binding.image)

            binding.image.setOnClickListener {
                binding.removeButton.visibility = View.VISIBLE
                binding.purchaseCount.visibility = View.VISIBLE

                var count = item.quantityInCart

                val purchaseLimit =
                    if (item.inShelf and (item.remainingInShelf < item.remainingInMachine)) {
                        item.remainingInShelf
                    } else {
                        item.remainingInMachine
                    }

                if (count < purchaseLimit) {
                    count += 1
                } else {
                    Toast.makeText(
                        context, "not enough items in " +
                                if (item.inShelf and (item.remainingInShelf < item.remainingInMachine)) {
                                    "Shelf"
                                } else {
                                    "Machine"
                                },
                        Toast.LENGTH_SHORT
                    ).show()
                }

                item.quantityInCart = count
                binding.purchaseCount.text = count.toString()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCardBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

}

class ItemDiffCallback: DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}