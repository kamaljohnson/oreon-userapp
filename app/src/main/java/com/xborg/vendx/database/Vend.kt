package com.xborg.vendx.database

enum class VendingState {
    Init,
    DeviceConnected,
    EncryptedOtpReceivedFromDevice,
    EncryptedOtpPlusBagReceivedFromServer,
    VendProgress,
    VendDone,
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
   var id: String = "",
   var status: VendingStatus = VendingStatus.Init,
   var bag: String = "",
   var mid: String = "",
   var uid: String = "",
   var encryptedOtp: String = "",
   var encryptedOtpPlusBag: String = "",
   var encryptedLog: String = "test log",
   var encryptedVendCompleteStatus: String = "",
   var timeStamp: String = ""
)
