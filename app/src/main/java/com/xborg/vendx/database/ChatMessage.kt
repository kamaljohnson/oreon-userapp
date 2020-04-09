package com.xborg.vendx.database

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("id") val id: String,
    @SerializedName("text") val text: String
)