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
import com.example.cashflow.data.repository.OutcomeRepository
import com.example.cashflow.domain.model.Outcome
import com.example.cashflow.presentation.ui.home.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class ExpensesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val outcomeRepo = OutcomeRepository(database.outcomeDao())
    private val _outcomes = mutableStateOf<UiState<List<Outcome>>>(UiState.Loading)
    val outcomes: State<UiState<List<Outcome>>> = _outcomes

    init {
        loadOutcomes()
    }

    // on new earning screen
    var showDateDialog by mutableStateOf(false)
    var title by mutableStateOf("")
    var details by mutableStateOf("")
    var amount by mutableStateOf("")
    var categoryId by mutableStateOf("")
    var accountId by mutableStateOf("")
    var selectedDate: LocalDate? by mutableStateOf(LocalDate.now())

    // Error states
    var titleError by mutableStateOf<String?>(null)
    var detailsError by mutableStateOf<String?>(null)
    var amountError by mutableStateOf<String?>(null)
    var categoryError by mutableStateOf<String?>(null)
    var accountError by mutableStateOf<String?>(null)
    var dateError by mutableStateOf<String?>(null)

    fun addOutcome(outcome: Outcome) {
        viewModelScope.launch(Dispatchers.IO) {
            outcomeRepo.addOutcome(outcome)
        }
    }

    fun loadOutcomes() {
        viewModelScope.launch(Dispatchers.IO) {
            _outcomes.value = UiState.Loading
            try {
                val result = outcomeRepo.getOutcomes().first()
                _outcomes.value = if (result.isNotEmpty()) UiState.Success(result)
                else UiState.Success(emptyList()) // or handle empty case
            } catch (e: Exception) {
                _outcomes.value = UiState.Error(e, e.message ?: "Unknown error")
            }
        }
    }

    // Reset form to default
    fun resetForm() {
        title = ""
        details = ""
        amount = ""
        categoryId = ""
        accountId = ""
        selectedDate = LocalDate.now()

        // Reset errors
        titleError = null
        detailsError = null
        amountError = null
        categoryError = null
        accountError = null
        dateError = null
    }

    // Form validation
    fun validateForm(): Boolean {
        // Validate title
        titleError = if (title.isBlank()) "Title can't be blank." else null

        // Validate amount (must be a positive number)
        val amountValue = amount.toDoubleOrNull()
        amountError = when {
            amount.isBlank() -> "Amount can't be blank."
            amountValue == null -> "Enter a valid number."
            amountValue <= 0.0 -> "Amount must be greater than 0."
            else -> null
        }

        // Validate details
        detailsError = if (details.length < 5) "Details must be at least 5 characters." else null

        // Validate category
        categoryError = if (categoryId.isBlank()) "Select a category." else null

        // Validate account
        accountError = if (accountId.isBlank()) "Select an account." else null

        // Validate date
        dateError = if (selectedDate == null) "Select a valid date." else null

        // Return true if all errors are null
        return listOf(
            titleError, amountError, detailsError, categoryError, accountError, dateError
        ).all { it == null }
    }

}
