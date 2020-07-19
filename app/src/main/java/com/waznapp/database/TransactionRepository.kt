package com.waznapp.database

import androidx.lifecycle.LiveData

class TransactionRepository (private val transactionDao : TransactionDAO){

    suspend fun insert (transaction : Transaction){
        transactionDao.insert(transaction)
    }

    fun getBalance () : LiveData<Double> {
        return transactionDao.getBalance()
    }

}