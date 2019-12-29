package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import kotlinx.android.synthetic.main.item_cart_slip.view.*

class ItemCartSlipAdapter(
    val items: List<Item>,
    val context: Context
) : RecyclerView.Adapter<ItemCartSlipAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_slip, parent, false)
        return ItemViewHolder(view)
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

        holder.itemLoc.text = if (item.inShelf) {
            "Shelf"
        } else {
            "Machine"
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
    class ItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.image
        val purchaseCount: TextView = view.purchase_count
        val itemLoc: TextView = view.item_loc
        val itemsInShelf: TextView = view.items_in_shelf
        var itemsInMachine: TextView = view.items_in_machine

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemsInShelf.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            addItemToCart()
        }

        private fun addItemToCart() {
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            val purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            if (purchaseLimitCount == purchaseCount) {
                return
            }

            if (purchaseCount == 0) {
                this.purchaseCount.visibility = View.VISIBLE
            }
            purchaseCount += 1
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
        }

        private fun removeItemFromCart() {
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            val purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            purchaseCount -= 1
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
            if (purchaseCount == 0) {
                this.purchaseCount.visibility = View.INVISIBLE
            }
        }
    }

}
