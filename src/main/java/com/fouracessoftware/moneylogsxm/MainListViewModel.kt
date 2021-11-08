package com.fouracessoftware.moneylogsxm

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.fouracessoftware.moneylogsxm.datadeal.Central
import com.fouracessoftware.moneylogsxm.datadeal.Txn
import kotlinx.coroutines.*

class MainListViewModel : ViewModel() {
    private var categoryListLD: LiveData<List<Category>> = MutableLiveData()

    private val categoryDao = Central.categoryDao!!
    private val txnDao = Central.txnDao !!
    private var lastErrLD:MutableLiveData<String> = MutableLiveData()

    fun getCategories(): LiveData<List<Category>> {
        return categoryListLD
    }

    fun getLastError():LiveData<String> {
        return lastErrLD
    }

    init {
        categoryListLD = categoryDao.getAllCategories().asLiveData()
        lastErrLD.value = "OK"
        /*
        CoroutineScope(Dispatchers.IO).launch {
            if(categoryListLD.value == null) {
                loadCategories()
            }
        }*/
    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch{
            categoryDao.insertCategory(Category(name="Housing"))
            categoryDao.insertCategory(Category(name = "Groceries"))
            categoryDao.insertCategory(Category(name = "Telecom"))
            categoryDao.insertCategory(Category(name = "Medical"))
            categoryDao.insertCategory(Category(name = "Health Insurance"))
            categoryDao.insertCategory(Category(name = "Transportation",description = "gas, fares, tolls"))
            categoryDao.insertCategory(Category(name = "Life Insurance"))
            categoryDao.insertCategory(Category(name = "Debt", description = "non-revolving"))
            categoryDao.insertCategory(Category(name = "Bank Fees"))
            categoryDao.insertCategory(Category(name = "Credit Card", description = "or other revolving-charge accounts"))
            categoryDao.insertCategory(Category(name = "Entertainment",description = "food out, movies, events, museums"))
        }
    }

    fun writeTxn(bare:Txn)
    {
        lastErrLD.value="WORKING"
        if(!validData(bare)) {
            return
        }
        //we store the function itself to be able to wait for results, to be dealt with in that try-catch block
        val wailted = CoroutineScope(Dispatchers.IO).async{
            if(bare.id < 1) {
                bare.id = 0
                txnDao.insertTxn(bare)
            }
            else
            {
                //TODO:updateTxn(bare)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {
                wailted.await()
                lastErrLD.postValue("OK")
            } catch(e:Exception) {
                println("CoroutineExceptionHandler got $e")
                lastErrLD.postValue("$e")
            }
        }
    }

    private fun validData(txnData: Txn): Boolean {
        if(txnData.who.isNullOrBlank()){
            lastErrLD.value = "To whom/which purpose this was paid?"
            return false
        }
        return true
    }
    // TODO: Implement the ViewModel
}