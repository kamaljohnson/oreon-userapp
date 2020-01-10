package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class VendingState {
    None,
    Init,
    EncryptedOtpReceived,
    EncryptedOtpPlusBagReceived,
    OtpValid,
    Vending,

    VendingComplete,

    OtpInvalid,
    VendingError,
}

enum class VendingStatus {
    Init,
    Processing,
    Done,

    Success,
    Failed
}

data class Vend (
    @Json(name = "Id") var id: String = "",
    @Json(name = "State") var state: VendingState = VendingState.None,
    @Json(name = "Bag") var bag: String = "",
    @Json(name = "MID") var mid: String = "",
    @Json(name = "UID") var uid: String = "",
    @Json(name = "EncryptedOtp") var encryptedOtp: String = "",
    @Json(name = "EncryptedOtpPlusBag") var encryptedOtpPlusBag: String = "",
    @Json(name = "EncryptedOtpPlusBag") var encryptedVendCompleteStatus: String = "",
    @Json(name = "TimeStamp") var timeStamp: String = ""
    )
