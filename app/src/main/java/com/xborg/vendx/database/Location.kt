package com.xborg.vendx.database

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

class Location(
    @ColumnInfo(name = "longitude")
    @SerializedName("Longitude")  var Longitude: Double,

    @ColumnInfo(name = "latitude")
    @SerializedName("Latitude")  var Latitude: Double
    )