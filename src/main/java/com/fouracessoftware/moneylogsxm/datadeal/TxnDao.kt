package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TxnDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTxn(txn: Txn):Long

    @Query("SELECT * FROM txn where date>= :firstdate AND date <= :lastdate")
    fun getTxns(firstdate:String,lastdate:String) : Flow<List<Txn>>

    @Query("SELECT id,who,amount,date,category_name FROM txn where category_name = :category AND date>= :firstdate AND date < :lastdate")
    fun getCategoryTxns(
        category: String,
        firstdate: String,
        lastdate: String
    ):List<Txn>
}