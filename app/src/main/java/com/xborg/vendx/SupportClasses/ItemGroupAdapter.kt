package com.xborg.vendx.SupportClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.Models.ItemGroupModel
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.item_group_holder.view.*

class ItemGroupAdapter(private val parents : List<ItemGroupModel>) : RecyclerView.Adapter<ItemGroupAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_group_holder, parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parent = parents[position]
        holder.textView.text = parent.title
        holder.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = ItemAdapter(parent.items)

            if(!parent.draw_line_breaker) {
                holder.lineBreaker.visibility = View.INVISIBLE
            }
            if(parent.title == "" ) run {
                val viewGroup: ViewGroup = holder.textView.parent as ViewGroup
                viewGroup.removeView(holder.textView)
            }
        }
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val recyclerView : RecyclerView = itemView.rv_item_group
        val textView:TextView = itemView.item_group_name_text
        val lineBreaker: ImageView = itemView.line_breaker
    }
}