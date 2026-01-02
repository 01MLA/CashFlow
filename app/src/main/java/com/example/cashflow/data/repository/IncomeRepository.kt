package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.IncomeDao
import com.example.cashflow.domain.model.Income

class IncomeRepository(private val incomeDao: IncomeDao) {
    suspend fun addIncome(income: Income) = incomeDao.addIncome(income)
    suspend fun addIncomes(incomes: List<Income>) = incomeDao.addIncomes(incomes)

    fun getIncomes() = incomeDao.getAllIncomes()
    suspend fun getAnIncomeById(id: Int) = incomeDao.getAnIncomeById(id)

    suspend fun updateIncome(income: Income) = incomeDao.updateIncome(income)

    suspend fun deleteAnIncome(id: Int) = incomeDao.deleteIncomeById(id)
    suspend fun deleteIncomes(ids: List<Int>) = incomeDao.deleteIncomes(ids)

    suspend fun deleteAllIncomes() = incomeDao.deleteAllIncomes()

}
