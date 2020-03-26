package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName


data class Feedback(
    @SerializedName("user")  var User: String = "",
    @SerializedName("topic")  var Topic: String = "",
    @SerializedName("message")  var Message: String = ""
)