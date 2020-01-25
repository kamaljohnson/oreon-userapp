package com.xborg.vendx.database

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Feedback(
    @Json(name = "UID") var uid: String = "",
    @Json(name = "Topic") var topic: String = "",
    @Json(name = "Body") var body: String = ""
    )