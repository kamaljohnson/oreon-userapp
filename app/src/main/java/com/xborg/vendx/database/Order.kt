package com.xborg.vendx.database

import com.squareup.moshi.Json

data class Order (
    @Json(name = "Id") var id: String,
    @Json(name = "Payable") var payable: Boolean,
    @Json(name = "Cost") var cost: Float,
    @Json(name = "Quantity") var quantity: Int
)