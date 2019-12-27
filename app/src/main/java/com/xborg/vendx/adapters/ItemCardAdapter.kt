package com.xborg.vendx.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import com.xborg.vendx.databinding.ItemCardBinding
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

class ItemCardAdapter(val items : List<Item>, val context: Context, val onitemListener: OnItemListener) :
    RecyclerView.Adapter<ItemCardAdapter.ItemViewHolder>() {

    val onItemListener: OnItemListener = onitemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ItemViewHolder(view, onItemListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.itemId.text = item.id
        holder.name.text = item.name
        holder.cost.text = item.cost.toString()
        Glide
            .with(context)
            .load(item.imgScrUrl)
            .into(holder.image)
    }


    class ItemViewHolder (view: View, onItemListener: OnItemListener)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.image
        val itemLimit: TextView = view.item_limit
        val purchaseCount: TextView = view.purchase_count

        val onItemListener: OnItemListener = onItemListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemListener.onItemClick(adapterPosition)
        }

    }

    interface OnItemListener {
        fun onItemClick(position: Int)
    }

}

