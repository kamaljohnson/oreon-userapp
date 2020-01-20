package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Application(
    @Json(name = "Version") val version: Int
)