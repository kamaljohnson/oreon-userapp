package com.xborg.vendx.database

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Application(
    @Json(name = "Version") val version: Int,
    @Json(name = "AlertMessage") val alertMessage: String
)