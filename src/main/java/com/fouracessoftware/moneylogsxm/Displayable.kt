package com.fouracessoftware.moneylogsxm

import com.fouracessoftware.moneylogsxm.datadeal.Txn

class Displayable(val category_name: String, val txnSet: List<Txn>) {
    fun sigma(): Float {
        var categoryTotal=0f
        for(i in txnSet) {
            categoryTotal+=i.amount
        }
        return categoryTotal
    }

}
