package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Machine(
    @SerializedName("id")  var Id: String = "",
    @SerializedName("name")  var Name: String = "Loading...",
    @SerializedName("mac")  var Mac: String = "",
    @SerializedName("location")  var Location: Location = Location(0.0, 0.0),
    @SerializedName("distance")  var Distance: Double = 0.0
    )