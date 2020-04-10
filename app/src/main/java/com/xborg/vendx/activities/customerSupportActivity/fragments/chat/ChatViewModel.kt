package com.xborg.vendx.activities.customerSupportActivity.fragments.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xborg.vendx.database.ChatMessage
import com.xborg.vendx.database.user.UserDao
import com.xborg.vendx.database.user.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ChatViewModel (
    application: Application
): AndroidViewModel(application) {

    val db = FirebaseFirestore.getInstance()

    val userDao: UserDao = UserDatabase.getInstance(application).userDao()

    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val chats = MutableLiveData<ArrayList<ChatMessage>>()
    var userId: String = ""

    fun autoLoadChats() {

        chats.value = ArrayList()

        ioScope.launch {

            db.collection("rooms")
                .document(userId)
                .collection("messages")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener { documents, e ->
                    if (e != null) {
                        Log.w("Debug", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val _chats = ArrayList<ChatMessage>()
                    for (document in documents!!) {

                        _chats.add(
                            ChatMessage(
                                userId = document["userId"] as String,
                                text = document["text"] as String,
                                time = document["time"] as Timestamp
                            )
                        )
                    }
                    chats.value = _chats
                }
        }
    }

    fun sendMessageToChat(message: String) {

        val chatMessage = ChatMessage(
            userId = userId,
            text = message,
            time = Timestamp.now()
        )


        db.collection("rooms")
            .document(userId)
            .collection("messages")
            .add(chatMessage)
            .addOnSuccessListener { documentReference ->
                Log.d("Debug","DocumentSnapshot added with ID: " + documentReference.id)
            }
            .addOnFailureListener { e -> Log.w("Debug", "Error adding document", e) }

    }
}
