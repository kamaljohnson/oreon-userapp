package com.xborg.vendx.adapters

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xborg.vendx.R
import com.xborg.vendx.database.ChatMessage
import com.xborg.vendx.database.user.UserDao
import com.xborg.vendx.database.user.UserDatabase
import kotlinx.android.synthetic.main.chat_message.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


private var TAG = "ChatMessageAdapter"

class ChatMessageAdapter(
    val context: Context
) : ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(ChatMessageDiffCallback()) {

    val userDao: UserDao = UserDatabase.getInstance(context).userDao()

    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_message, parent, false)

        return ChatMessageViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat", "ResourceAsColor")
    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = getItem(position)

        holder.text.text = chatMessage.text

        val sfd = SimpleDateFormat("HH:mm")
        holder.time.text = sfd.format(chatMessage.time.toDate());

        ioScope.launch {

            val userId = userDao.get().Id

            if(chatMessage.userId != userId) {

                holder.messageBackground.setBackgroundResource(R.drawable.drawable_message_box_received);
                val params = holder.messageBackground.layoutParams as RelativeLayout.LayoutParams
                params.removeRule(RelativeLayout.ALIGN_PARENT_END);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                holder.messageBackground.layoutParams = params
            }
        }
    }

    class ChatMessageViewHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val messageBackground: LinearLayout = view.message_background
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

