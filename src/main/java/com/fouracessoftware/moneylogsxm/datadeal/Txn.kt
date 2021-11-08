package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.*
@Entity(indices = [Index(value = ["category_name"]),Index(value = ["date"])])

data class Txn(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id:Long,
    var who:String,
    var date:String,
    var amount:Float,
    var category_name:String,
    var notes:String? = null
) {

}
