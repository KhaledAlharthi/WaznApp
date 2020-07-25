package com.waznapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TransactionRepository (private val transactionDao : TransactionDAO){




    suspend fun insert (transaction : Transaction): TransactionViewModel.TransactionState {
        transactionDao.insert(transaction)
        return TransactionViewModel.TransactionState.Done
    }

    fun getBalance () : LiveData<Double> {
        return transactionDao.getBalance()
    }


}