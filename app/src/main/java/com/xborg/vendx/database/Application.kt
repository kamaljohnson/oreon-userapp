package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

class Application(
    @SerializedName("Version")  val Version: Int,
    @SerializedName("AlertMessage") val AlertMessage: String
)