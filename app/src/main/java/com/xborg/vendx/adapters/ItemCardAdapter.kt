package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.Item
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

class ItemCardAdapter(
    val items: List<Item>,
    val context: Context,
    itemCardListener: OnItemListener
) : RecyclerView.Adapter<ItemCardAdapter.ItemViewHolder>() {

    private val _onCardItemListener: OnItemListener = itemCardListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return ItemViewHolder(view, _onCardItemListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.itemId.text = item.Id
        holder.name.text = item.Name
        holder.cost.text = "₹ " + item.Cost.toString()
        Glide
            .with(context)
            .load(item.ImgScrUrl)
            .into(holder.image)

        holder.itemLoc.text = if (item.InShelf) {
            "Shelf"
        } else {
            "Machine"
        }

        if (item.InShelf) {
            holder.cost.visibility = View.GONE
        }

        holder.itemsInMachine.text = item.RemainingInMachine.toString()
        holder.itemsInShelf.text = item.RemainingInShelf.toString()
        holder.itemsInShelf.visibility = if (item.InShelf) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        holder.outOfStock.visibility = if (!item.InMachine) {
            View.INVISIBLE
        } else if(item.RemainingInMachine == 0){
            View.VISIBLE
        } else {
            View.INVISIBLE
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
        var outOfStock: ImageView = view.out_of_stock_icon

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
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            var purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            val itemsInShelfInt = itemsInShelf.text.toString().toInt()
            val itemsInMachineInt = itemsInMachine.text.toString().toInt()

            Log.i(TAG, "item state:  in machine : $itemsInMachineInt from shelf : $itemsInShelfInt")

            when (itemLoc.text) {
                "Shelf" -> {
                    purchaseLimitCount = if (itemsInMachineInt < itemsInShelfInt) {
                        itemsInMachineInt
                    } else {
                        itemsInShelfInt
                    }
                }
                "Machine" -> {
                    purchaseLimitCount = itemsInMachineInt
                }
            }

            if (purchaseLimitCount == purchaseCount) {
                displayItemLimitReached(itemView.context)
                return
            }

            if (onItemListener.onItemAddedToCart(itemId.text.toString(), itemLoc.text.toString())) {
                if (purchaseCount == 0) {
                    this.purchaseCount.visibility = View.VISIBLE
                    itemRemoveButton.visibility = View.VISIBLE
                }
                purchaseCount += 1
            } else {
                displayItemLimitReached(itemView.context)
            }
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
        }

        private fun removeItemFromCart() {
            var purchaseCount = this.purchaseCount.text.toString().split("/")[0].toInt()
            val purchaseLimitCount = this.purchaseCount.text.toString().split("/")[1].toInt()

            if (onItemListener.onItemRemovedFromCart(
                    itemId.text.toString(),
                    itemLoc.text.toString()
                )
            ) {
                purchaseCount -= 1
            }
            this.purchaseCount.text = "$purchaseCount/$purchaseLimitCount"
            if (purchaseCount == 0) {
                this.purchaseCount.visibility = View.INVISIBLE
                itemRemoveButton.visibility = View.INVISIBLE
            }
        }

        private fun displayItemLimitReached(context: Context) {
            Toast.makeText(context, "item not remaining in machine", Toast.LENGTH_SHORT).show()
        }

    }

    interface OnItemListener {
        fun onItemAddedToCart(itemId: String, itemLoc: String): Boolean
        fun onItemRemovedFromCart(itemId: String, itemLoc: String): Boolean
    }
}

