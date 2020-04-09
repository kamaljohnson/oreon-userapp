package com.xborg.vendx.database

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val text: String,
    val time: Timestamp
)