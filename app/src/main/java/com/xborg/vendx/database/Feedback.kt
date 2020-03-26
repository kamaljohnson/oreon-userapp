package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

enum class FeedbackTopic {
    @SerializedName("UI") UI,
    @SerializedName("VE") Vending,
    @SerializedName("VU") Vulnerability,
    @SerializedName("PY") Payment,
    @SerializedName("OT") Other,
}

data class Feedback(
    @SerializedName("user")  var User: String = "",
    @SerializedName("topic")  var Topic: FeedbackTopic,
    @SerializedName("message")  var Message: String = ""
)