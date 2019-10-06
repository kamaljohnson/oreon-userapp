package com.xborg.vendx

import android.content.Context
import android.service.autofill.OnClickAction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.inventory_item.view.*

private var TAG = "ItemAdapter"

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
        if (holder.cost != null) holder.cost.text = "₹ " + items[position].cost
        if (holder.quantity != null) holder.quantity.text = items[position].quantity
        if (holder.items_left != null) {
            if(items[position].items_left == "") {
                holder.items_left.text = ""
            } else {
                    holder.items_left.text = items[position].items_left + " left"
            }
        }

    }
}

class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    var name = view.name
    var cost = view.cost
    var quantity = view.quantity
    var items_left = view.items_left

    var add_button = view.add_button
    var remove_button = view.remove_button

    init {
        add_button.setOnClickListener{
            Log.d(TAG, "add button clicked")
            var count = view.purchase_count.text.toString().toInt()
            if(count == 0){
                view.purchase_count.visibility = View.VISIBLE
                remove_button.visibility = View.VISIBLE
            }
            count+=1
            view.purchase_count.text = count.toString()
        }

        remove_button.setOnClickListener{
            Log.d(TAG, "remove button clicked")
            var count = view.purchase_count.text.toString().toInt()
            if(count == 1){
                view.purchase_count.visibility = View.INVISIBLE
                remove_button.visibility = View.INVISIBLE
            }
            count-=1
            view.purchase_count.text = count.toString()
        }
    }
}

class Item {
    lateinit var name:String
    lateinit var cost:String
    lateinit var quantity:String
    lateinit var items_left:String
}