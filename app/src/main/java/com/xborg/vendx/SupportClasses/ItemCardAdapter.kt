package com.xborg.vendx.SupportClasses

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.models.ItemModel
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

var previous_view: View? = null

class ItemAdapter(val items: ArrayList<ItemModel>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item_card views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

    // Binds each item_card in the ArrayList to a view
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.item_id.text = items[position].item_id
        holder.name.text = items[position].name
        if(items[position].cost != "-1") {                  //non-shelf item
            holder.cost.text = "₹ ${items[position].cost}"
            holder.item_limit.text = "10"
            holder.item_limit.visibility = View.INVISIBLE

            if(MainActivity.cart_items[items[position].item_id] != null) {
                holder.purchase_count.text = MainActivity.cart_items[items[position].item_id].toString()
                if(holder.purchase_count.text.toString() != "0") {
                    holder.purchase_count.visibility = View.VISIBLE
                }
            }
        } else {                                            //shelf item
            holder.cost.text = "-1"
            holder.cost.visibility = View.INVISIBLE
            holder.item_limit.text = items[position].item_limit
            holder.item_limit.visibility = View.VISIBLE

            if(MainActivity.cart_items_from_shelf[items[position].item_id] != null) {
                holder.purchase_count.text = MainActivity.cart_items_from_shelf[items[position].item_id].toString()
                if(holder.purchase_count.text.toString() != "0") {
                    holder.purchase_count.visibility = View.VISIBLE
                }
            }
        }

        holder.image.isClickable = items[position].selectable

        Glide
            .with(holder.context)
            .load(items[position].image_src)
            .into(holder.image)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var item_id = view.item_id
        var name = view.name
        var cost = view.cost
        var image = view.image

        var item_limit = view.item_limit

        var purchase_count = view.purchase_count
        var remove_button = view.remove_button
//        var info_button= view.info_button

        var context = itemView.getContext()

        init {
                if(purchase_count.text == "0") {
                purchase_count.visibility = View.INVISIBLE
            }

            remove_button.visibility = View.INVISIBLE
//            info_button.visibility = View.VISIBLE

            if(MainActivity.cart_items.count() > 0) {

            }

            Log.e(TAG, "item_card count : " + purchase_count.text.toString())

            if(cost.text == "-1") {
                Log.e(TAG, "cost not shown")
                cost.visibility = View.INVISIBLE
            }

            view.rootView.setOnClickListener {
                if(!MainActivity.get_button_lock) {
                    var count = purchase_count.text.toString().toInt()

                    previous_view = view

                    if(count == item_limit.text.toString().split(' ')[0].toInt()) {
                        Toast.makeText(context, "Item purchase limit reached", Toast.LENGTH_SHORT).show()
                    }

                    purchase_count.visibility = View.VISIBLE
                    remove_button.visibility = View.VISIBLE

//                    if(cart_count_text_view!!.text == "0") {
//                        cart_count_text_view.visibility = View.VISIBLE
//                    }

                    if(count < item_limit.text.toString().split(' ')[0].toInt()) {
                        count+=1
//                        cart_count_text_view!!.text = (cart_count_text_view.text.toString().toInt() + 1).toString()
                    }

                    if(cost.text == "-1") {
                        MainActivity.cart_items_from_shelf[view.item_id.text.toString()] = count
                    } else {
                        MainActivity.cart_items[view.item_id.text.toString()] = count
                    }
                    purchase_count.text = count.toString()
                }
            }

            remove_button.setOnClickListener{
                var count = purchase_count.text.toString().toInt()
                if(count > 0) {
                    count-=1
//                    cart_count_text_view!!.text = (cart_count_text_view.text.toString().toInt() - 1).toString()
                }
                if(cost.text == "-1") {
                    MainActivity.cart_items_from_shelf[view.item_id.text.toString()] = count
                } else {
                    MainActivity.cart_items[view.item_id.text.toString()] = count
                }

                if(count == 0){
                    remove_button.visibility = View.INVISIBLE
                    purchase_count.visibility = View.INVISIBLE
    //                info_button.visibility = View.INVISIBLE
                    MainActivity.cart_items.remove(item_id.text)
//                    if(cart_count_text_view!!.text == "0") {
//                        cart_count_text_view.visibility = View.INVISIBLE
//                    }
                }
                purchase_count.text = count.toString()
//                if(cart_count_text_view!!.text.toString().toInt() == 0) {
//    //                TODO: set visibility of action button to invisible
//                }

            }

//            info_button.setOnClickListener{
//                val intent = Intent(context, ItemInfoActivity::class.java)
//                intent.putExtra("item_id", item_id.text.toString())
//                context.startActivity(intent)
//            }
        }
    }
}

