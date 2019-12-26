package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xborg.vendx.activities.mainActivity.MainActivity
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

class ItemAdapter(items: List<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    var data = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: View = view
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.image
        val itemLimit: TextView = view.item_limit
        val purchaseCount: TextView = view.purchase_count
        val removeButton: ImageView = view.remove_button
        val context: Context = itemView.context

        init {
            view.purchase_count.visibility = View.INVISIBLE
            view.remove_button.visibility = View.INVISIBLE
        }

        fun bind(item: Item) {
            itemId.text = item.id
            name.text = item.name

            if (!item.inMachine) {
                card.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shop_basket_icon
                    )
                )
                card.isClickable = false
            }

            if (item.inShelf) {
                cost.text = "-1"
                cost.visibility = View.INVISIBLE
                itemLimit.text =item.remainingInShelf.toString()
                itemLimit.visibility = View.VISIBLE

                if (MainActivity.cart_items_from_shelf[item.id] != null) {
                    purchaseCount.text =
                        MainActivity.cart_items_from_shelf[item.id].toString()
                    if (purchaseCount.text.toString() != "0") {
                        purchaseCount.visibility = View.VISIBLE
                    }
                }

            } else {
                cost.text = "₹ ${item.cost}"
                itemLimit.text = "10"
                itemLimit.visibility = View.INVISIBLE

                if (MainActivity.cart_items[item.id] != null) {
                    purchaseCount.text = MainActivity.cart_items[item.id].toString()
                    if (purchaseCount.text.toString() != "0") {
                        purchaseCount.visibility = View.VISIBLE
                    }
                }
            }

            image.isClickable = item.inMachine

            Glide
                .with(context)
                .load(item.imgScrUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.blank_image)
                        .error(R.drawable.broken_image)
                )
                .into(image)

            image.setOnClickListener {
                removeButton.visibility = View.VISIBLE
                purchaseCount.visibility = View.VISIBLE

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
                purchaseCount.text = count.toString()
            }
        }
    }
}