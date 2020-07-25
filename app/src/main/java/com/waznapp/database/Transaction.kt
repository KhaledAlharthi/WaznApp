package com.waznapp.database

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.InverseBindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.waznapp.BR
import java.util.*

@Entity (tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val id : Int,               // Auto-generated unique value

    var amount : Double,        // Amount of transaction

    var type : String,          // Type of transaction (PURCHASE, TRANSFER, SALARY)

    var userExtraInfo : String, // User can provide additional information about the transaction

    var merchant : String,      // Merchant name

    var purpose : String,       // General purpose of transaction (groceries, car maintenance, investment .. etc)

    var priority : String,      // User can evaluate the priority of this transaction (HIGH, MEDIUM, LOW)

    var currency : String,      // Transaction currency

    var date : Long             // Date of transaction
) : BaseObservable() {


    // Empty constructor
    constructor() : this (0, 0.0, "", "",  "", "", "", "", 0)

    override fun toString(): String {
        return "id: ${id}, " +
                "Amount: ${amount}," +
                " Type: ${type}," +
                " userExtraInfo: ${userExtraInfo}," +
                " Merchant: ${merchant}," +
                " Purpose: ${purpose}," +
                " Priority: ${priority}," +
                " Currency: ${currency}," +
                " Date (timestamp): ${date}"
    }
}