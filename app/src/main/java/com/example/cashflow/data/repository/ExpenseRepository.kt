package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.ExpenseDao
import com.example.cashflow.domain.model.Expense

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun addExpense(expense: Expense) = expenseDao.addExpense(expense)

    fun getExpenses() = expenseDao.getAllExpenses()

    suspend fun getAnExpenseById(id: Int) = expenseDao.getAnExpenseById(id)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(id: Int) = expenseDao.deleteExpenseById(id)

    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()

}
