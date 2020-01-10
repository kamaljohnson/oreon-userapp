package com.xborg.vendx.database

import com.squareup.moshi.Json

enum class TransactionType{
    Order,
    Payment,
    Vend
}

enum class TransactionStatus {
    Processing,
    Successful,
    Failed,
    Temp    //TODO: remove after adding status to orders
}

data class Transaction(
    @Json(name = "Id") var id: String,
    @Json(name = "Type") var type: TransactionType,
    @Json(name = "Status") var status: TransactionStatus,
    @Json(name = "TimeStamp") var timeStamp: String = ""
    )

data class TransactionList(
    @Json(name = "Transactions") var transactions: List<Transaction>
)