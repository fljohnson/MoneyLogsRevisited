package com.fouracessoftware.moneylogsxm.datadeal

import android.content.Context
import androidx.room.*

@Database(entities = [Category::class, Txn::class], version=1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun txnDao(): TxnDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "TheMoneyLogs")
                .createFromAsset("database/TheMoneyLogsProto.db")
                .build()
        }
    }
}