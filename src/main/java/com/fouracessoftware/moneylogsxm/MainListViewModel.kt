package com.fouracessoftware.moneylogsxm

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.fouracessoftware.moneylogsxm.datadeal.Central
import com.fouracessoftware.moneylogsxm.datadeal.Txn
import com.fouracessoftware.moneylogsxm.Displayable
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class MainListViewModel : ViewModel() {

    private var bigListLD: MutableLiveData<List<Displayable>> = MutableLiveData()
    private var categoryListLD: LiveData<List<Category>> = MutableLiveData()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val categoryDao = Central.categoryDao!!
    private val txnDao = Central.txnDao !!
    private var lastErrLD:MutableLiveData<String> = MutableLiveData()

    fun getCategoriesAndTxns():LiveData<List<Displayable>> {
        return bigListLD
    }
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
        if(txnData.who.isBlank()){
            lastErrLD.value = "To whom/which purpose this was paid?"
            return false
        }
        return true
    }

    fun loadTxnsPerCategory() {
        var cal=Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH,1)
        var firstdate = dateFormat.format(cal)
        cal.add(Calendar.MONTH,1)
        var afterdate = dateFormat.format(cal) //first of the following month
        var categorySet: List<Category>
        var txnSet:List<Txn>
        var rv:ArrayList<Displayable> = arrayListOf()
        CoroutineScope(Dispatchers.IO).launch {
            categorySet = categoryDao.getCategories()
            for (i in categorySet){
               txnSet = txnDao.getCategoryTxns(i.name,firstdate,afterdate)
               rv.add(Displayable(i.name,txnSet))
            }
            bigListLD.postValue(rv)
        }
    }


}