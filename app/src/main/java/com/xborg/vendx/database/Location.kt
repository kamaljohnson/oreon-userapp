package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("Longitude")  var Longitude: Double,
    @SerializedName("Latitude")  var Latitude: Double
    )