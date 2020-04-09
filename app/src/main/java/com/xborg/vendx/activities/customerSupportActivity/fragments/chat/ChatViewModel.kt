package com.xborg.vendx.activities.customerSupportActivity.fragments.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.xborg.vendx.database.ChatMessage
import com.xborg.vendx.database.user.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel (
    application: Application
): AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()
    private val userDao = UserDatabase.getInstance(application).userDao()

    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val chats = MutableLiveData<ArrayList<ChatMessage>>()
    var initChatLoaded = MutableLiveData<Boolean>()

    init {
        initChatLoaded.value = false
        loadPreviousChats()
    }

    private fun loadPreviousChats() {

        chats.value = ArrayList()

        var userId: String

        ioScope.launch {

//            Log.i("Debug", userDao.get()!!.value.toString())
//
//            userId = userDao.get()!!.value!!.Id

            db.collection("rooms")
                .document("1")
                .collection("messages")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.i("Debug", "message: ${document.data}")
                            val message = ChatMessage(
                                id = document.id,
                                text = document.data["text"] as String
                            )
                            chats.value!!.add(message)
                        }
                        initChatLoaded.value = true
                    } else {
                        Log.i("Debug", "Error getting documents.", task.exception)
                    }
                }
        }
    }

    fun sendMessageToChat(message: ChatMessage) {

    }
}
