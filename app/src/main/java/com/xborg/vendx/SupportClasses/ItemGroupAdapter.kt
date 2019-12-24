package com.xborg.vendx.SupportClasses

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.models.ItemGroupModel
import com.xborg.vendx.R
import kotlinx.android.synthetic.main.item_group_holder.view.*

class ItemGroupAdapter(private val parents : ArrayList<ItemGroupModel>) : RecyclerView.Adapter<ItemGroupAdapter.GroupViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_group_holder, parent,false)
        Log.i("TAG", "onCreateViewHolder")

        return GroupViewHolder(v)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val parent = parents[position]
        holder.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = ItemAdapter(parent.items)

            Log.i("TAG", "onBindViewHolder")

            if(!parent.draw_line_breaker) {
                holder.lineBreaker.visibility = View.INVISIBLE
            }
        }
    }

    inner class GroupViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val recyclerView : RecyclerView = itemView.rv_item_group
        val lineBreaker: ImageView = itemView.line_breaker
    }
}