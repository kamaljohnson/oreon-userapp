package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class BagStatus {
    Init,
    OtpReceived,
    OtpValid,
    OtpInvalid,
    CartPassed,
    Vending,
    Compelte,
    VendingError
}

data class Bag(
    @Json(name = "EncryptedOtp") var encryptedOtp: String,
    @Json(name = "Status") var status: BagStatus,
    @Json(name = "Bag") var bag: String
)