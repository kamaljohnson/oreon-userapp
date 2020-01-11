package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class VendingState {
    Init,
    DeviceConnected,
    EncryptedOtpReceivedFromDevice,
    EncryptedOtpPlusBagReceivedFromServer,
    Vending,
    VendingDone,
    EncryptedDeviceLogReceivedFromDevice,
    EncryptedVendStatusReceivedFromServer,
    VendingComplete,
}

enum class VendingStatus {
    Init,
    Processing,
    Done,

    Success,
    Failed
}

data class Vend(
    @Json(name = "Id") var id: String = "",
    @Json(name = "State") var status: VendingStatus = VendingStatus.Init,
    @Json(name = "Bag") var bag: String = "",
    @Json(name = "MID") var mid: String = "",
    @Json(name = "UID") var uid: String = "",
    @Json(name = "EncryptedOtp") var encryptedOtp: String = "",
    @Json(name = "EncryptedOtpPlusBag") var encryptedOtpPlusBag: String = "",
    @Json(name = "EncryptedLog") var encryptedLog: String = "test log",
    @Json(name = "EncryptedVendCompleteStatus") var encryptedVendCompleteStatus: String = "",
    @Json(name = "TimeStamp") var timeStamp: String = ""
)
