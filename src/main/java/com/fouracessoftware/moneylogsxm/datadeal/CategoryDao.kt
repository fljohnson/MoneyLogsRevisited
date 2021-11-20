package com.fouracessoftware.moneylogsxm.datadeal

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.fouracessoftware.moneylogsxm.datadeal.Category
@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(category: Category):Long

    @Query("SELECT * FROM category")
    fun getAllCategories() : Flow<List<Category>>


    @Query("SELECT name,description FROM category")
    fun getCategories():List<Category>
}