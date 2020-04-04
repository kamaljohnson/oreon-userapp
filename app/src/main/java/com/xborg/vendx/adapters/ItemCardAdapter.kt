package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xborg.vendx.R
import com.xborg.vendx.database.InventoryItem
import com.xborg.vendx.database.ItemDetailDao
import com.xborg.vendx.database.ItemDetailDatabase
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private var TAG = "ItemCardAdapter"

class ItemCardAdapter(
    private val inventoryItems: List<InventoryItem>,
    private val paidItemGroup: Boolean,
    val context: Context,
    itemCardListener: OnItemListener
) : RecyclerView.Adapter<ItemCardAdapter.ItemViewHolder>() {

    private val _onCardItemListener: OnItemListener = itemCardListener

    private lateinit var itemDetailDao: ItemDetailDao

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)

        itemDetailDao = ItemDetailDatabase.getInstance(context).itemDetailDatabaseDao

        return ItemViewHolder(view, _onCardItemListener)
    }

    override fun getItemCount(): Int {
        return inventoryItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val inventoryItem = inventoryItems[position]

        val viewModelJob = Job()
        val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

        ioScope.launch {
            Log.i(TAG, "loading itemDetail : $inventoryItem from room database")

            val itemDetail = itemDetailDao.get(inventoryItem.ItemDetailId)

            holder.itemId.text = itemDetail!!.Id
            holder.name.text = itemDetail.Name
            holder.cost.text = "₹ " + itemDetail.Cost.toString()

            Glide
                .with(context)
                .load(itemDetail.ForegroundAsset)
                .into(holder.packageImage)
            Glide
                .with(context)
                .load(itemDetail.BackgroundAsset)
                .into(holder.cardBg)
        }

        holder.paid = paidItemGroup

    }

    @SuppressLint("SetTextI18n")
    class ItemViewHolder(view: View, onItemListener: OnItemListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val packageImage: ImageView = view.package_image
        val cardBg: ImageView = view.card_bg
        private val purchaseCount: TextView = view.purchase_count

        private val itemRemoveButton: ImageView = view.remove_button

        private val onItemListener: OnItemListener = onItemListener

        var paid: Boolean = false

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemRemoveButton.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
            itemRemoveButton.setOnClickListener {
                removeItemFromCart()
            }
        }

        override fun onClick(v: View?) {
            addItemToCart()
        }

        private fun addItemToCart() {
            onItemListener.onItemAddedToCart(itemId.text.toString(), paid)
        }

        private fun removeItemFromCart() {
            onItemListener.onItemRemovedFromCart(itemId.text.toString(), paid)
        }

    }

    interface OnItemListener {
        fun onItemAddedToCart(itemId: String, paid: Boolean): Boolean
        fun onItemRemovedFromCart(itemId: String, paid: Boolean): Boolean
    }
}

