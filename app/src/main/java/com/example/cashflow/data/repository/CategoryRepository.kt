package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.CategoryDao
import com.example.cashflow.domain.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun addCategory(category: Category) = categoryDao.addCategory(category)
    suspend fun getCategoryId(title: String) = categoryDao.getCategoryId(title)

    suspend fun getCategories() = categoryDao.getAllCategories()

    suspend fun getACategoryById(id: Int) = categoryDao.getACategoryById(id)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteACategory(id: String) = categoryDao.deleteCategoryById(id)

    suspend fun deleteAllCategories() = categoryDao.deleteAllCategories()
}
