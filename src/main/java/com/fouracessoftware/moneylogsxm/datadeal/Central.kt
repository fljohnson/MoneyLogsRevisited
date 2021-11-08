package com.fouracessoftware.moneylogsxm.datadeal

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Central {
    var categoryDao:CategoryDao? = null
    var txnDao: TxnDao? = null

    fun activate(applicationContext: Context?) {
        if(categoryDao == null) {
            categoryDao = AppDatabase.getInstance(applicationContext!!).categoryDao()

            CoroutineScope(Dispatchers.IO).launch {
                categoryDao!!.insertCategory(Category("Housing"))
                categoryDao!!.insertCategory(Category("Groceries"))
                categoryDao!!.insertCategory(Category("Telecom", "Internet, email, pay TV, telephone"))
                categoryDao!!.insertCategory(Category("Medical","Office visits, prescriptions"))
                categoryDao!!.insertCategory(Category("Health Insurance"))
                categoryDao!!.insertCategory(Category("Transportation","gas, fares, tolls"))
                categoryDao!!.insertCategory(Category("Life Insurance"))
                categoryDao!!.insertCategory(Category("Debt","non-revolving"))
                categoryDao!!.insertCategory(Category("Bank Fees"))
                categoryDao!!.insertCategory(Category("Credit Card","or other revolving-charge accounts"))
                categoryDao!!.insertCategory(Category("Entertainment","food out, movies, events, museums"))

            }
        }
        if(txnDao == null) {
            txnDao = AppDatabase.getInstance(applicationContext!!).txnDao()
        }

    }
}