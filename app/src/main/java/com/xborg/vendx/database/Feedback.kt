package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName


data class Feedback(
    @SerializedName("Uid")  var Uid: String = "",
    @SerializedName("Topic")  var Topic: String = "",
    @SerializedName("Body")  var Body: String = ""
)