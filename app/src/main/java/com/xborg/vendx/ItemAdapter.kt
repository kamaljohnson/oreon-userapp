package com.xborg.vendx

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item.view.*

private var TAG = "ItemAdapter"

var previous_view: View? = null

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
        holder.item_id.text = items[position].item_id
        holder.name.text = items[position].name
        if(items[position].cost != "-1") {
            holder.cost.text = "₹ ${items[position].cost}"
            holder.item_limit.text = "10 left"
        } else {
            holder.cost.text = ""
            holder.item_limit.text = items[position].item_limit + " left"
        }

        if(HomeActivity.cart_items[items[position].item_id] != null) {
            holder.purchase_count.text = HomeActivity.cart_items[items[position].item_id].toString()
            holder.purchase_count.visibility = View.VISIBLE
        }

        Glide
            .with(context)
            .load(items[position].image_src)
            .into(holder.image)
    }
}

class ItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    var item_id = view.item_id
    var name = view.name
    var cost = view.cost
    var image = view.image

    var item_limit = view.item_limit

    var purchase_count = view.purchase_count
    var add_button = view.add_button
    var remove_button = view.remove_button
    var info_button= view.info_button

    var context = itemView.getContext()

    init {
            if(purchase_count.text == "0") {
            purchase_count.visibility = View.INVISIBLE
        }

        add_button.visibility = View.INVISIBLE
        remove_button.visibility = View.INVISIBLE
        info_button.visibility = View.INVISIBLE

        if(HomeActivity.cart_items.count() > 0) {

        }

        Log.e(TAG, "item count : " + purchase_count.text.toString())

        if(cost.text == "-1") { //prevents displaying the item cost in shelf activity
            cost.text = ""
        }

        image.setOnClickListener{
            var count = purchase_count.text.toString().toInt()

            if(previous_view != view && previous_view != null) {
                previous_view!!.add_button.visibility = View.INVISIBLE
                previous_view!!.remove_button.visibility = View.INVISIBLE
                previous_view!!.info_button.visibility = View.INVISIBLE
            }

            previous_view = view

            if(add_button.visibility == View.INVISIBLE) {
                add_button.visibility = View.VISIBLE
                remove_button.visibility = View.VISIBLE
                info_button.visibility = View.VISIBLE
            }

            if(count == 0){
                purchase_count.visibility = View.VISIBLE
                remove_button.visibility = View.VISIBLE
            }
            if(count < item_limit.text.toString().split(' ')[0].toInt()) {
                count+=1
            }

            if(count == item_limit.text.toString().split(' ')[0].toInt()) {
                Toast.makeText(context, "You have reached the purchase limit for this item", Toast.LENGTH_SHORT).show()
            }

            HomeActivity.cart_items[view.item_id.text.toString()] = count
            purchase_count.text = count.toString()
        }

        add_button.setOnClickListener{
            var count = purchase_count.text.toString().toInt()

            if(count == 0){
                purchase_count.visibility = View.VISIBLE
                remove_button.visibility = View.VISIBLE
            }
            if(count < item_limit.text.toString().split(' ')[0].toInt()) {
                count+=1
            }

            if(count == item_limit.text.toString().split(' ')[0].toInt()) {
                Toast.makeText(context, "You have reached the purchase limit for this item", Toast.LENGTH_SHORT).show()
            }

            HomeActivity.cart_items[view.item_id.text.toString()] = count

            purchase_count.text = count.toString()
        }

        remove_button.setOnClickListener{
            var count = purchase_count.text.toString().toInt()
            count-=1
            add_button.visibility = View.VISIBLE
            HomeActivity.cart_items[view.item_id.text.toString()] = count
            if(count == 0){
                add_button.visibility = View.INVISIBLE
                remove_button.visibility = View.INVISIBLE
                purchase_count.visibility = View.INVISIBLE
                HomeActivity.cart_items.remove(view.item_id.toString())
            }
            purchase_count.text = count.toString()
        }

        info_button.setOnClickListener{
            val intent = Intent(context, ItemInfoActivity::class.java)
            intent.putExtra("item_id", item_id.text.toString())
            context.startActivity(intent)
        }
    }
}