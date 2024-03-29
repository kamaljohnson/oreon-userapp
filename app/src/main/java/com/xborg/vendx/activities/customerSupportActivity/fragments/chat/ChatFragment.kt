package com.xborg.vendx.activities.customerSupportActivity.fragments.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xborg.vendx.R
import com.xborg.vendx.adapters.ChatMessageAdapter
import kotlinx.android.synthetic.main.chat_fragment.*

class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ChatViewModelFactory(activity!!.application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatViewModel::class.java)

        val chatMessageAdapter = ChatMessageAdapter(context!!)

        messageRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatMessageAdapter
        }

        viewModel.chats.observe(viewLifecycleOwner, Observer { chats ->
            if(chats != null) {
                chatMessageAdapter.submitList(chats)
                progressBar.visibility = View.GONE
                messageRecyclerView.smoothScrollToPosition(chats.count())
            }
        })

        sendButton.setOnClickListener {

            val message = messageEditText.text.toString()
            if(message != "") {
                viewModel.sendMessageToChat(message)

                messageEditText.setText("")
                messageRecyclerView.smoothScrollToPosition(viewModel.chats.value!!.count())
            }

        }

    }

}
