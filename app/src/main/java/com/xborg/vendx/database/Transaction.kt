package com.xborg.vendx.database

import com.google.gson.annotations.SerializedName

enum class TransactionType{
    Order,
    Payment,
    Vend
}

enum class TransactionStatus {
    @SerializedName("Processing")  Processing,
    @SerializedName("Successful")  Successful,
    @SerializedName("Failed")  Failed,
    @SerializedName("Temp")  Temp    //TODO: remove after adding status to orders
}

data class Transaction(
    @SerializedName("Id")  var Id: String,
    @SerializedName("Type")  var Type: TransactionType,
    @SerializedName("Status")  var Status: TransactionStatus,
    @SerializedName("TimeStamp")  var TimeStamp: String = ""
)

data class TransactionList(
    var Transactions: List<Transaction>
)