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
            .load(item.ForegroundAsset)
            .into(holder.packageImage)
        Glide
            .with(context)
            .load(item.BackgroundAsset)
            .into(holder.cardBg)
        Glide
            .with(context)
            .load(item.ContentAsset)
            .into(holder.infoImg)

        holder.itemLoc.text = if (item.FromInventory) {
            "Inventory"
        } else {
            "Machine"
        }

        if (item.FromInventory) {
            holder.cost.visibility = View.GONE
        }

        holder.itemsInMachine.text = item.MachineStock.toString()
        holder.itemsInInventory.text = item.InventoryStock.toString()
        holder.itemsInInventory.visibility = if (item.FromInventory) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        holder.outOfStock.visibility = if (!item.FromMachine) {
            View.INVISIBLE
        } else if(item.MachineStock == 0){
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
        val packageImage: ImageView = view.package_image
        val cardBg: ImageView = view.card_bg
        val infoImg: ImageView = view.info_img
        private val purchaseCount: TextView = view.purchase_count
        val itemLoc: TextView = view.item_loc
        val itemsInInventory: TextView = view.items_in_inventory
        var itemsInMachine: TextView = view.items_in_machine
        var outOfStock: ImageView = view.out_of_stock_icon

        private val itemRemoveButton: ImageView = view.remove_button

        private val onItemListener: OnItemListener = onItemListener

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemRemoveButton.visibility = View.INVISIBLE
            itemsInInventory.visibility = View.INVISIBLE

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

            val itemsInInventoryInt = itemsInInventory.text.toString().toInt()
            val itemsInMachineInt = itemsInMachine.text.toString().toInt()

            Log.i(TAG, "item state:  in machine : $itemsInMachineInt from inventory : $itemsInInventoryInt")

            when (itemLoc.text) {
                "Inventory" -> {
                    purchaseLimitCount = if (itemsInMachineInt < itemsInInventoryInt) {
                        itemsInMachineInt
                    } else {
                        itemsInInventoryInt
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

