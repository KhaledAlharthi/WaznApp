package com.waznapp.database

import android.app.Application
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.waznapp.BR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TransactionViewModel (application : Application) : AndroidViewModel(application) {
    private val TAG = javaClass.simpleName

    // Define transaction state (default to idle)
    val transactionState = MutableLiveData<TransactionState>().apply { value =
        TransactionState.None
    }

    // Object of data repository
    private val repository : TransactionRepository

    // Saves current user balance.
    val balance : LiveData<Double>

    lateinit var pendingTransaction : Transaction

    init {
        val transactionDao = TransactionDatabase.getDatabase(application, viewModelScope).transactionDao()
        repository = TransactionRepository(transactionDao)
        balance = repository.getBalance()

    }


    /**
    *   Invokes insertion of transaction into db.
    *   @param transaction Transaction to be inserted.
    * */
    fun insert (transaction: Transaction) = viewModelScope.launch (Dispatchers.IO){
        Log.e(TAG, "${transaction}")
        transactionState.postValue(TransactionState.Loading)
        delayFun()
        // Perform actual inserting of transaction.
        val newState = repository.insert(transaction)
        /**
        * Check the result of insertion
        * if transaction was inserted, change transaction state to Done to update UI
        * otherwise, change transaction state to Failed, to update UI accordingly
        * */
        transactionState.postValue(newState)
    }


    /**
     *  Delay function to demonstrate network call
     * */
    suspend fun delayFun () {
        delay(2500)
    }


    /*
    *   Class of different possible states of Transaction
    * */
    sealed class TransactionState {
        object None : TransactionState ()
        object Loading : TransactionState()
        object Done : TransactionState()
        object Failed : TransactionState()
    }

}