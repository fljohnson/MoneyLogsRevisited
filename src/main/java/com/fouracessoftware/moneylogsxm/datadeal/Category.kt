package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.*
@Entity(indices = [Index(value = ["name"],unique = true)])

data class Category(
    @ColumnInfo
    @PrimaryKey
    val name:String,
    val description:String? = null
) {
    override fun toString(): String {
        if(description.isNullOrEmpty())
        {
            return name
        }
        return "$name ($description)"
    }
}