package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Location(
    @Json(name = "Longitude") var longitude: Float,
    @Json(name = "Latitude") var latitude: Float
    )