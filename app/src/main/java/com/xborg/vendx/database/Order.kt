package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Order (
    @Json(name = "Id") var id: String,
    @Json(name = "Amount") var payable: Float = 0f
)