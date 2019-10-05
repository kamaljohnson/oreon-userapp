package com.xborg.vendx

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.inventory_item.view.*

class ItemAdapter(val items : ArrayList<Item>, val context: Context) : RecyclerView.Adapter<ItemViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder.name != null) holder.name.text = items[position].name
        if (holder.cost != null) holder.cost.text = items[position].cost + " Rs"
        if (holder.quantity != null) holder.quantity.text = items[position].quantity
    }
}

class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    var name = view.name
    var cost = view.cost
    var quantity = view.quantity
}

class Item {
    lateinit var name:String
    lateinit var cost:String
    lateinit var quantity:String
}