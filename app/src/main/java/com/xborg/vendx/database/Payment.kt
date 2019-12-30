package com.xborg.vendx.database

import com.squareup.moshi.Json
import com.xborg.vendx.activities.paymentActivity.PaymentStatus

data class Payment(
    @Json(name = "Id") var id: String = "",
    @Json(name = "Status") var status: PaymentStatus,
    @Json(name = "Signature") var signature: String = "",
    @Json(name = "PaymentId") var paymentId: String = "",
    @Json(name = "OrderId") var orderId: String,
    @Json(name = "Rnd") var rnd: String = "",
    @Json(name = "UID") var uid: String
)