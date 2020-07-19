package com.waznapp.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel (application : Application) : AndroidViewModel(application) {

    private val repository : TransactionRepository


    val balance : LiveData<Double>

    init {
        val transactionDao = TransactionDatabase.getDatabase(application, viewModelScope).transactionDao()
        repository = TransactionRepository(transactionDao)
        balance = repository.getBalance()
        Log.d("ViewModel", "Balance ${balance.value}")
    }


    fun insert (transaction: Transaction) = viewModelScope.launch (Dispatchers.IO){
        repository.insert(transaction)
    }
}