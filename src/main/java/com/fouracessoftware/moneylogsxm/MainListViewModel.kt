package com.fouracessoftware.moneylogsxm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fouracessoftware.moneylogsxm.datadeal.Category
import com.fouracessoftware.moneylogsxm.datadeal.Central
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainListViewModel : ViewModel() {
    private var categoryListLD: LiveData<List<Category>> = MutableLiveData()

    private val categoryDao = Central.categoryDao!!
    fun getCategories(): LiveData<List<Category>> {
        return categoryListLD
    }

    init {
        categoryListLD = categoryDao.getAllCategories().asLiveData()
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
    // TODO: Implement the ViewModel
}