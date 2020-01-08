package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

class ItemCardAdapter(
    val items: List<Item>,
    val context: Context,
    val onitemListener: OnItemListener
) :
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
        holder.cost.text = "₹ " + item.cost.toString()
        Glide
            .with(context)
            .load(item.imgScrUrl)
            .into(holder.image)

        holder.itemLoc.text = if (item.inShelf) {
            "Shelf"
        } else {
            "Machine"
        }

        if(item.inShelf) {
            holder.cost.visibility = View.GONE
        }
        
        holder.itemsInMachine.text = item.remainingInMachine.toString()
        holder.itemsInShelf.text = item.remainingInShelf.toString()
        holder.itemsInShelf.visibility = if (item.remainingInShelf == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    class ItemViewHolder(view: View, onItemListener: OnItemListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.image
        val purchaseCount: TextView = view.purchase_count
        val itemLoc: TextView = view.item_loc
        val itemsInShelf: TextView = view.items_in_shelf
        var itemsInMachine: TextView = view.items_in_machine

        val itemRemoveButton: ImageView = view.remove_button

        val onItemListener: OnItemListener = onItemListener

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemRemoveButton.visibility = View.INVISIBLE
            itemsInShelf.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
            itemRemoveButton.setOnClickListener {
                removeItemFromCart()
            }
        }

        override fun onClick(v: View?) {
            addItemToCart()
        }

        private fun addItemToCart() {
            onItemListener.onItemAddedToCart(itemId.text.toString(), itemLoc.text.toString())
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            val purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            if (purchaseLimitCount == purchaseCount) {
                return
            }

            if (purchaseCount == 0) {
                this.purchaseCount.visibility = View.VISIBLE
                itemRemoveButton.visibility = View.VISIBLE
            }
            purchaseCount += 1
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
        }

        private fun removeItemFromCart() {
            onItemListener.onItemRemovedFromCart(itemId.text.toString(), itemLoc.text.toString())
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            val purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            purchaseCount -= 1
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
            if (purchaseCount == 0) {
                this.purchaseCount.visibility = View.INVISIBLE
                itemRemoveButton.visibility = View.INVISIBLE
            }
        }
    }

    interface OnItemListener {
        fun onItemAddedToCart(itemId: String, itemLoc: String)
        fun onItemRemovedFromCart(itemId: String, itemLoc: String)
    }
}

