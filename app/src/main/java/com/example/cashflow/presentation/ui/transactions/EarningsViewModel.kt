package com.example.cashflow.presentation.ui.transactions

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflow.CashFlowApplication
import com.example.cashflow.data.repository.IncomeRepository
import com.example.cashflow.domain.model.Income
import com.example.cashflow.presentation.ui.home.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class EarningsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val incomeRepo = IncomeRepository(database.incomeDao())

    //
    private val _incomes = mutableStateOf<UiState<List<Income>>>(UiState.Loading)
    val incomes: State<UiState<List<Income>>> = _incomes

    init {
        loadIncomes()
    }

    // on new earning screen
    var showDateDialog by mutableStateOf(false)
    var title by mutableStateOf("")
    var details by mutableStateOf("")
    var amount by mutableStateOf("")
    var categoryId by mutableStateOf("")
    var accountId by mutableStateOf("")
    var selectedDate: LocalDate? by mutableStateOf(LocalDate.now())

    fun addIncome(income: Income) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeRepo.addIncome(income)
        }
    }

    fun loadIncomes() {
        viewModelScope.launch(Dispatchers.IO) {
            _incomes.value = UiState.Loading
            try {
                val result = incomeRepo.getIncomes().first()
                _incomes.value = if (result.isNotEmpty()) UiState.Success(result)
                else UiState.Success(emptyList()) // or handle empty case
            } catch (e: Exception) {
                _incomes.value = UiState.Error(e, e.message ?: "Unknown error")
            }
        }
    }

}
