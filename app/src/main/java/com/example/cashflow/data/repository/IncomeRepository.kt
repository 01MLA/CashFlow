package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.IncomeDao
import com.example.cashflow.domain.model.Income

class IncomeRepository(private val incomeDao: IncomeDao) {
    suspend fun addIncome(income: Income) = incomeDao.addIncome(income)

    fun getIncomes() = incomeDao.getAllIncomes()

    suspend fun getAnIncomeById(id: String) = incomeDao.getAnIncomeById(id)

    suspend fun updateIncome(income: Income) = incomeDao.updateIncome(income)

    suspend fun deleteAnIncome(id: String) = incomeDao.deleteIncomeById(id)

    suspend fun deleteAllIncomes() = incomeDao.deleteAllIncomes()

}
