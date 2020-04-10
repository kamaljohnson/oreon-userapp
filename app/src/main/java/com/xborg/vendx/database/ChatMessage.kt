package com.xborg.vendx.database

import com.google.firebase.Timestamp

data class ChatMessage(
    val text: String,
    val time: Timestamp
)