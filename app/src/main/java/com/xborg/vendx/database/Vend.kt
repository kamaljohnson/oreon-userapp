package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

enum class VendingState {

    //ble connection states
    Init,
    DeviceDiscovered,
    ConnectionRequest,
    Connecting,
    Connected,

    //vending states
    ReceivedOtp,
    ReceivedOtpWithBag,
    Vending,
    VendingDone,
    ReceivedLog,
    SentLogAck,
    VendingComplete,

    //error states
    Error,
}
enum class VendingStatus {
    @SerializedName("Init")  Init,
    @SerializedName("Processing")  Processing,
    @SerializedName("Done")  Done,
    @SerializedName("Success")  Success,
    @SerializedName("Failed")  Failed
}

data class Vend(
    @SerializedName("Id")  var Id: String = "",
    @SerializedName("Status")  var Status: VendingStatus = VendingStatus.Init,
    @SerializedName("Bag")  var Bag: String = "",
    @SerializedName("Mid")  var mid: String = "",
    @SerializedName("Uid")  var Uid: String = "",
    @SerializedName("EncryptedOtp")  var EncryptedOtp: String = "",
    @SerializedName("EncryptedOtpPlusBag")  var EncryptedOtpPlusBag: String = "",
    @SerializedName("EncryptedLog")  var EncryptedLog: String = "test log",
    @SerializedName("EncryptedVendCompleteStatus")  var EncryptedVendCompleteStatus: String = "",
    @SerializedName("TimeStamp")  var TimeStamp: String = ""
)
