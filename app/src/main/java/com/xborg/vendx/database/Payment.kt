package com.xborg.vendx.database

enum class PaymentState {
    None,
    OrderInit,
    UserCheckout,
    OrderIdReceived,
    PaymentInit,
    PaymentDone,
    PaymentTokenCreated,    //TODO: this state should be removed
    PaymentPosted,
    PaymentComplete,

    PaymentRetry
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
    var id: String = "",
    var amount: Float = 0f,
    var status: PaymentStatus = PaymentStatus.None,
    var signature: String = "",
    var razorpayPaymentId: String = "",
    var razorpayOrderId: String = "",       //TODO: update this while posting order
    var orderId: String,
    var rnd: String = "",                               //TODO: this should be changed with razorpayOrderId | just a temp fix
    var uid: String = "",
    var timeStamp: String = ""
)