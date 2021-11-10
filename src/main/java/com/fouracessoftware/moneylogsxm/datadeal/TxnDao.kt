package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TxnDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTxn(txn: Txn):Long

    @Query("SELECT * FROM txn where date>= :firstdate AND date <= :lastdate")
    fun getTxns(firstdate:String,lastdate:String) : Flow<List<Txn>>

    @Query("SELECT * FROM txn where category_name = :category AND date>= :firstdate AND date < :lastdate ORDER BY date")
    fun getCategoryTxns(
        category: String,
        firstdate: String,
        lastdate: String
    ):List<Txn>

    @Query("SELECT * FROM txn WHERE id = :xid")
    suspend fun getSingleTxn(xid:Long):Txn

    @Update
    suspend fun updateTxn(txn:Txn)
}