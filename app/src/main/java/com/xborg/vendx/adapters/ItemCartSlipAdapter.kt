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
import com.xborg.vendx.database.CartItem
import kotlinx.android.synthetic.main.item_cart_slip.view.*

class ItemCartSlipAdapter(
    val cart: List<CartItem>,
    val context: Context
) : RecyclerView.Adapter<ItemCartSlipAdapter.ItemSlipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_slip, parent, false)
        return ItemSlipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    override fun onBindViewHolder(holder: ItemSlipViewHolder, position: Int) {
        val cart = cart[position]

//        holder.itemId.text = item.Id
//        holder.name.text = item.Name
//        holder.cost.text =  "₹ " + (item.Cost * item.CartCount).toString()
//        Glide
//            .with(context)
//            .load(item.ForegroundAsset)
//            .into(holder.image)
//
//        holder.itemLoc.text = if (item.FromInventory) {
//            "Shelf"
//        } else {
//            "Machine"
//        }
//
//        if(item.FromInventory) {
//            holder.itemsInInventory.visibility = View.VISIBLE
//            holder.itemsInInventory.text = item.CartCount.toString()
//            holder.cost.visibility = View.INVISIBLE
//            holder.paidIcon.visibility = View.VISIBLE
//        } else {
//            holder.itemsInMachine.visibility = View.VISIBLE
//            holder.itemsInMachine.text = item.CartCount.toString()
//        }
    }

    @SuppressLint("SetTextI18n")
    class ItemSlipViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.package_image
        private val purchaseCount: TextView = view.purchase_count
        val paidIcon: ImageView = view.paid_icon

        init {
            purchaseCount.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //TODO: can be used in the future
        }
    }

}
