package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.ItemGroup
import kotlinx.android.synthetic.main.item_group_holder.view.*

class ItemGroupAdapter(
    val items: ArrayList<ItemGroup>,
    val context: Context,
    val onItemListener: ItemCardAdapter.OnItemListener
) : RecyclerView.Adapter<ItemGroupAdapter.GroupViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group_holder, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val parent = items[position]
        holder.title.text = parent.title
        if(parent.showNoMachinesNearbyMessage) {
            holder.noMachinesNearMessage.visibility = View.VISIBLE
        } else {
            holder.groupItemsRV.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = ItemCardAdapter(parent.items, context, onItemListener)

                if (!parent.drawLineBreaker) {
                    holder.itemView.line_breaker.visibility = View.INVISIBLE
                }
            }
        }
        holder.lineBreaker.visibility = if (parent.drawLineBreaker) {
            View.INVISIBLE
        } else {
            View.INVISIBLE
        }
    }

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupItemsRV: RecyclerView = view.rv_item_group
        val lineBreaker: ImageView = view.line_breaker
        val title: TextView = view.title
        val noMachinesNearMessage: RelativeLayout = view.no_machines_near_message
        val context: Context = itemView.context
    }

    interface OnItemListener {
        fun onRefreshRequest(itemId: String, itemLoc: String): Boolean
    }


}
