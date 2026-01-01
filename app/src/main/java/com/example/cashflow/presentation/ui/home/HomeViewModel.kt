package com.example.cashflow.presentation.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflow.CashFlowApplication
import com.example.cashflow.data.repository.ExpenseRepository
import com.example.cashflow.data.repository.IncomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val incomeRepo = IncomeRepository(database.incomeDao())
    private val expenseRepo = ExpenseRepository(database.expenseDao())

    val incomes =
        incomeRepo.getIncomes().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val expenses =
        expenseRepo.getExpenses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
