package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TxnDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTxn(txn: Txn):Long

    @Query("SELECT * FROM txn where date>= :firstdate AND date <= :lastdate")
    fun getTxns(firstdate:String,lastdate:String) : Flow<List<Txn>>
}