package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.CategoryDao
import com.example.cashflow.domain.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun addCategory(category: Category) = categoryDao.addCategory(category)
    fun getCategoryById(title: String) = categoryDao.getCategoryId(title)

    fun getCategories() = categoryDao.getAllCategories()

    fun getACategoryById(id: String) = categoryDao.getACategoryById(id)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteACategory(id: String) = categoryDao.deleteCategoryById(id)

    suspend fun deleteAllCategories() = categoryDao.deleteAllCategories()
}
