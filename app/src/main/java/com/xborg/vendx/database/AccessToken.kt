package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

data class AccessToken(
//    this token will be populated when using email login
    @SerializedName("token") val token: String,

//    this fields will be populated when using oauth login
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Number,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("scope") val scope: String,
    @SerializedName("refresh_token") val refreshToken: String
)