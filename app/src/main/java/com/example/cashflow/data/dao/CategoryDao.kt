package com.example.cashflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashflow.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addCategory(category: Category): Long

    @Query("SELECT id FROM categories WHERE title = :title")
    abstract fun getCategoryId(title: String): Int

    @Query("SELECT * FROM categories WHERE id = :id")
    abstract fun getACategoryById(id: String): Flow<Category?>

    @Query("SELECT * FROM categories")
    abstract fun getAllCategories(): Flow<List<Category>>

    @Update
    abstract suspend fun updateCategory(category: Category): Int

    @Query("DELETE FROM categories")
    abstract suspend fun deleteAllCategories(): Int

    @Query("DELETE FROM categories WHERE id = :id")
    abstract suspend fun deleteCategoryById(id: String): Int

}
