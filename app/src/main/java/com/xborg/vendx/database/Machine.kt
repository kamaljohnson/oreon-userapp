package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class Machine(
    @SerializedName("Id")  var Id: String = "",
    @SerializedName("Code")  var Code: String = "Loading...",
    @SerializedName("Mac")  var Mac: String = "",
    @SerializedName("Location")  var Location: Location = Location(0.0, 0.0),
    @SerializedName("Distance")  var Distance: Double = 0.0
    )