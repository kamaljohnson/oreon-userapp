package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class BagStatus {
    None,
    Init,
    EncryptedOtpReceived,
    EncryptedOtpPlusBagReceived,
    OtpValid,
    Vending,

    Complete,

    OtpInvalid,
    VendingError,
}

data class Bag (
    @Json(name = "EncryptedOtp") var encryptedOtp: String = "",
    @Json(name = "Status") var status: BagStatus = BagStatus.None,
    @Json(name = "Bag") var bag: String = "",
    @Json(name = "MID") var mid: String = "",
    @Json(name = "UID") var uid: String = "",
    @Json(name = "EncryptedOtpPlusBag") var encryptedOtpPlusBag: String = "",
    @Json(name = "TimeStamp") var timeStamp: String = ""
    )