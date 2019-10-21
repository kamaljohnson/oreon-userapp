package com.xborg.vendx

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item.view.*

private var TAG = "ItemAdapter"

class ItemAdapter(val items : ArrayList<Item>, val context: Context) : RecyclerView.Adapter<ItemViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    // Binds each item in the ArrayList to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        holder.item_id.text = items[position].item_id
        holder.name.text = items[position].name
        if(items[position].cost != "-1") {
            holder.cost.text = "₹ ${items[position].cost}"
        } else {
            holder.cost.text = ""
        }

        Glide
            .with(context)
            .load(items[position].image_src)
            .into(holder.image)
    }
}

class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
//    var item_id = view.item_id
    var name = view.name
    var cost = view.cost
    var image = view.image

    var item_limit = TextView(view.context)

    var purchase_count = view.item_count
    var add_button = view.add_button
    var remove_button = view.remove_button

    init {
        item_limit.text = "10"
        var count = purchase_count.text.toString().toInt()

        add_button.setOnClickListener{
            Log.d(TAG, "add button clicked")
            if(count == 0){
                purchase_count.visibility = View.VISIBLE
                remove_button.visibility = View.VISIBLE
            }
            if(count < item_limit.text.toString().split(' ')[0].toInt()) {
                count+=1
            } else {
                add_button.visibility = View.INVISIBLE
            }

//            InventoryActivity.cart_items[view.item_id.text.toString()] = count
            purchase_count.text = count.toString()
        }

        remove_button.setOnClickListener{
            Log.d(TAG, "remove button clicked")
            count-=1
            add_button.visibility = View.VISIBLE
//            InventoryActivity.cart_items[view.item_id.text.toString()] = count
            if(count == 0){
                purchase_count.visibility = View.INVISIBLE
                remove_button.visibility = View.INVISIBLE
//                InventoryActivity.cart_items.remove(view.item_id.toString())
            }
            purchase_count.text = count.toString()
        }
    }
}