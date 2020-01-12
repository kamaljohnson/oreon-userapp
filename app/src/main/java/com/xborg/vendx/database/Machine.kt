package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Machine(
    @Json(name = "Id") var id: String,
    @Json(name = "Code") var code: String,
    @Json(name = "Location") var location: Location
    )