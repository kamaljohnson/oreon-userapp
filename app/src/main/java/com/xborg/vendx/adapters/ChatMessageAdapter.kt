package com.xborg.vendx.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.ChatMessage
import kotlinx.android.synthetic.main.chat_message.view.*
import java.text.SimpleDateFormat

private var TAG = "ChatMessageAdapter"

class ChatMessageAdapter(
    val context: Context
) : ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(ChatMessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message, parent, false)

        return ChatMessageViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = getItem(position)

        holder.text.text = chatMessage.text

        val sfd = SimpleDateFormat("HH:mm")
        holder.time.text = sfd.format(chatMessage.time.toDate());


    }

    class ChatMessageViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val time: TextView = view.time
        val text: TextView = view.message_text

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }

    }
}

class ChatMessageDiffCallback: DiffUtil.ItemCallback<ChatMessage>() {
    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}

