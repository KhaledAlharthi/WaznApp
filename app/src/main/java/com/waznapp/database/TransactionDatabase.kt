package com.waznapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database (entities = arrayOf(Transaction::class), version = 3, exportSchema = false)
public abstract class TransactionDatabase : RoomDatabase() {


    abstract fun transactionDao(): TransactionDAO



    private class TransactionDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            mInstance?.let { database ->
                scope.launch {
                    val transactionDao = database.transactionDao()

                    // Delete all content here.
                    //transactionDao.deleteAllTransactions()
                }
            }
        }
    }




    companion object {

        @Volatile
        private var mInstance : TransactionDatabase? = null


        fun getDatabase (context : Context,
        scope: CoroutineScope) : TransactionDatabase {
            return mInstance?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                )
                    .addCallback(TransactionDatabaseCallback(scope))
                    .build()
                mInstance = instance
                instance
            }
        }

    }
}