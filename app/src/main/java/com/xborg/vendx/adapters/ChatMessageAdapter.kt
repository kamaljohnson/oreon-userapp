package com.xborg.vendx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.*

private var TAG = "ChatMessageAdapter"

class ChatMessageAdapter(
    val context: Context
) : ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(ChatMessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message, parent, false)

        return ChatMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = getItem(position)

    }

    class ChatMessageViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }

    }
}

class ChatMessageDiffCallback: DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}

