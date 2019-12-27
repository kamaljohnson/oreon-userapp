package com.xborg.vendx.adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
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

        holder.itemLoc.text = if(item.inShelf) { "Shelf" } else { "Machine" }
    }

    class ItemViewHolder (view: View, onItemListener: OnItemListener)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.image
        val itemLimit: TextView = view.item_limit
        val purchaseCount: TextView = view.purchase_count
        val itemLoc: TextView = view.item_loc

        val itemRemoveButton: ImageView = view.remove_button

        val onItemListener: OnItemListener = onItemListener
        init {
            itemView.setOnClickListener(this)
            itemRemoveButton.setOnClickListener {
                onItemListener.onItemRemovedFromCart(itemId.text.toString(), itemLoc.text.toString())
            }
        }

        override fun onClick(v: View?) {
            onItemListener.onItemAddedToCart(itemId.text.toString(), itemLoc.text.toString())
        }
    }

    interface OnItemListener {
        fun onItemAddedToCart(itemId: String, itemLoc: String)
        fun onItemRemovedFromCart(itemId: String, itemLoc: String)
    }

}

