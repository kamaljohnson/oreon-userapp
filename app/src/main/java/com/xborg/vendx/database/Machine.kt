package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Machine(
    @Json(name = "Id") var id: String = "",
    @Json(name = "Code") var code: String = "Loading...",
    @Json(name = "Location") var location: Location = Location(0.0, 0.0),
    @Json(name = "Distance") var distance: Double = 0.0
    )