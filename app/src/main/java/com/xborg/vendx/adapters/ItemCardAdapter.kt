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
import com.xborg.vendx.database.ItemDetail
import kotlinx.android.synthetic.main.item_card.view.*

private var TAG = "ItemCardAdapter"

class ItemCardAdapter(
    val items: List<ItemDetail>,
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

    @SuppressLint("SetTextI18n")
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
            onItemListener.onItemAddedToCart(itemId.text.toString())
        }

        private fun removeItemFromCart() {
            onItemListener.onItemRemovedFromCart(itemId.text.toString())
        }

    }

    interface OnItemListener {
        fun onItemAddedToCart(itemId: String): Boolean
        fun onItemRemovedFromCart(itemId: String): Boolean
    }
}

