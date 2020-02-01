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
) : RecyclerView.Adapter<ItemCartSlipAdapter.ItemSlipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlipViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart_slip, parent, false)
        return ItemSlipViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemSlipViewHolder, position: Int) {
        val item = items[position]

        holder.itemId.text = item.Id
        holder.name.text = item.Name
        holder.cost.text =  "₹ " + (item.Cost * item.cartCount).toString()
        Glide
            .with(context)
            .load(item.PackageImageUrl)
            .into(holder.image)

        holder.itemLoc.text = if (item.InInventory) {
            "Shelf"
        } else {
            "Machine"
        }

        if(item.InInventory) {
            holder.itemsInInventory.visibility = View.VISIBLE
            holder.itemsInInventory.text = item.cartCount.toString()
            holder.cost.visibility = View.INVISIBLE
            holder.paidIcon.visibility = View.VISIBLE
        } else {
            holder.itemsInMachine.visibility = View.VISIBLE
            holder.itemsInMachine.text = item.cartCount.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    class ItemSlipViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemId: TextView = view.item_id
        val name: TextView = view.name
        val cost: TextView = view.cost
        val image: ImageView = view.package_image
        private val purchaseCount: TextView = view.purchase_count
        val itemLoc: TextView = view.item_loc
        val itemsInInventory: TextView = view.items_in_inventory
        var itemsInMachine: TextView = view.items_in_machine
        val paidIcon: ImageView = view.paid_icon

        init {
            purchaseCount.visibility = View.INVISIBLE
            itemsInInventory.visibility = View.INVISIBLE
            itemsInMachine.visibility = View.INVISIBLE

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            //TODO: can be used in the future
        }
    }

}
