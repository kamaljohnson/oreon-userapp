package com.xborg.vendx.SupportClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemSlipAdapter"

class ItemSlipAdapter(val items : ArrayList<Item>, val context: Context) : RecyclerView.Adapter<ItemSlipViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item_card views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlipViewHolder {
        return ItemSlipViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.item_slip, parent, false)
        )
    }

    // Binds each item_card in the ArrayList to a view
    override fun onBindViewHolder(holder: ItemSlipViewHolder, position: Int) {
        holder.item_id.text = items[position].item_id
        holder.name.text = items[position].name
        holder.item_limit.text = items[position].item_limit

        Glide
            .with(context)
            .load(items[position].image_src)
            .into(holder.image)
    }
}

class ItemSlipViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    var item_id = view.item_id
    var name = view.name
    var image = view.image
    var item_limit = view.item_limit

    var context = itemView.getContext()
}