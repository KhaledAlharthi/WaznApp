package com.waznapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    var id : Int,               // Auto-generated unique value

    val amount : Double,        // Amount of transaction

    val type : String,          // Type of transaction (PURCHASE, TRANSFER, SALARY)

    val userExtraInfo : String, // User can provide additional information about the transaction

    val merchant : String?,      // Merchant name

    val purpose : String,       // General purpose of transaction (groceries, car maintenance, investment .. etc)

    val priority : String,      // User can evaluate the priority of this transaction (HIGH, MEDIUM, LOW)

    val currency : String,      // Transaction currency

    val date : Long             // Date of transaction
)