package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class PaymentState {
    None,
    OrderInit,
    UserCheckout,
    OrderIdReceived,
    PaymentInit,
    PaymentDone,
    PaymentTokenCreated,    //TODO: this state should be removed
    PaymentPosted,
    PaymentFinished,

    PaymentRetry,
    PaymentFailed
}

enum class PaymentStatus {
    None,
    Init,
    Processing,
    SuccessfulLocal,
    Successful,
    Failed
}

data class Payment(
    @Json(name = "Id") var id: String,
    @Json(name = "Amount") var amount: Float = 0f,
    @Json(name = "Status") var status: PaymentStatus = PaymentStatus.None,
    @Json(name = "Signature") var signature: String = "",
    @Json(name = "RazorpayPaymentId") var razorpayPaymentId: String = "",
    @Json(name = "RazorpayOrderId") var razorpayOrderId: String = "",       //TODO: update this while posting order
    @Json(name = "OrderId") var orderId: String,
    @Json(name = "RND") var rnd: String = "",                               //TODO: this should be changed with razorpayOrderId | just a temp fix
    @Json(name = "UID") var uid: String = ""
)