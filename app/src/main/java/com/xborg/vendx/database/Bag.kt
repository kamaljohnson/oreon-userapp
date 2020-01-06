package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class BagStatus {
    None,
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
    @Json(name = "EncryptedOtp") var encryptedOtp: String = "",
    @Json(name = "Status") var status: BagStatus = BagStatus.None,
    @Json(name = "Bag") var bag: String = ""
)