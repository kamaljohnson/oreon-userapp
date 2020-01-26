package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

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
    @SerializedName("None")  None,
    @SerializedName("Init")  Init,
    @SerializedName("Processing")  Processing,
    @SerializedName("SuccessfulLocal")  SuccessfulLocal,
    @SerializedName("Successful")  Successful,
    @SerializedName("Failed")  Failed
}

data class Payment(
    @SerializedName("Id")  var Id: String? = "",
    @SerializedName("Amount")  var Amount: Float = 0f,
    @SerializedName("Status")  var Status: PaymentStatus = PaymentStatus.None,
    @SerializedName("Signature")  var Signature: String = "",
    @SerializedName("RazorpayPaymentId")  var RazorpayPaymentId: String = "",
    @SerializedName("RazorpayOrderId")  var RazorpayOrderId: String = "",       //TODO: update this while posting order
    @SerializedName("OrderId")  var OrderId: String,
    @SerializedName("Rnd")  var Rnd: String? = "",                               //TODO: this should be changed with razorpayOrderId | just a temp fix
    @SerializedName("Uid")  var Uid: String = "",
    @SerializedName("TimeStamp")  var TimeStamp: String = ""
)