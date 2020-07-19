package com.waznapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDAO {


    @Query ("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions (): LiveData<List<Transaction>>

    @Query ("DELETE FROM transactions")
    suspend fun deleteAllTransactions()


    @Query ("SELECT SUM(amount) FROM transactions")
    fun getBalance () : LiveData<Double>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Update
    suspend fun update (transaction: Transaction)

}