package com.xborg.vendx.database

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
    var id: String,
    var type: TransactionType,
    var status: TransactionStatus,
    var timeStamp: String = ""
)

data class TransactionList(
    var transactions: List<Transaction>
)