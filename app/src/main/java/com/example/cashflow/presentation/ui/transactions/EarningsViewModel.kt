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
import com.example.cashflow.data.repository.AccountRepository
import com.example.cashflow.data.repository.CategoryRepository
import com.example.cashflow.data.repository.IncomeRepository
import com.example.cashflow.domain.model.Account
import com.example.cashflow.domain.model.Category
import com.example.cashflow.domain.model.Income
import com.example.cashflow.presentation.ui.home.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class EarningsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val incomeRepo = IncomeRepository(database.incomeDao())
    private val categoryRepo = CategoryRepository(database.categoryDao())
    private val accountRepo = AccountRepository(database.accountDao())

    // UiState for incomes
    private val _incomes = mutableStateOf<UiState<List<Income>>>(UiState.Loading)
    val incomes: State<UiState<List<Income>>> = _incomes

    // Accounts state
    private val _accounts = mutableStateOf<List<Account>>(emptyList())
    val accounts: State<List<Account>> = _accounts

    // Categories state
    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories

    init {
        loadAccounts()
        loadCategories()
        loadIncomes()
    }

    var showDateDialog by mutableStateOf(false)

    // Form fields
    var title by mutableStateOf("")
    var amount by mutableStateOf("") // keep as String to bind with TextField
    var details by mutableStateOf("")
    var category by mutableStateOf("")
    var account by mutableStateOf("")
    var selectedCategory by mutableStateOf("Select Category")
    var selectedAccount by mutableStateOf("Select Account")

    var selectedDate: LocalDate? by mutableStateOf(LocalDate.now())

    // Error states
    var formErrorMessage by mutableStateOf<String?>(null)

    var titleError by mutableStateOf<String?>(null)
    var detailsError by mutableStateOf<String?>(null)
    var amountError by mutableStateOf<String?>(null)
    var categoryError by mutableStateOf<String?>(null)
    var accountError by mutableStateOf<String?>(null)
    var dateError by mutableStateOf<String?>(null)

    // Add income to database
    fun addIncome(income: Income) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeRepo.addIncome(income)
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _accounts.value = accountRepo.getAccounts().first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _categories.value = categoryRepo.getCategories().first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Load incomes from database
    fun loadIncomes() {
        viewModelScope.launch(Dispatchers.IO) {
            _incomes.value = UiState.Loading
            delay(1200)
            try {
                val result = incomeRepo.getIncomes().first()
                _incomes.value = UiState.Success(result)
            } catch (e: Exception) {
                _incomes.value = UiState.Error(e, e.message ?: "Unknown error")
            }
        }
    }

    // Reset form to default
    fun resetForm() {
        title = ""
        details = ""
        amount = ""
        category = ""
        account = ""
        selectedCategory = "Select Category"
        selectedAccount = "Select Account"
        selectedDate = LocalDate.now()

        // Reset errors
        titleError = null
        detailsError = null
        amountError = null
        categoryError = null
        accountError = null
        dateError = null

        // Reset the errorMessage
        formErrorMessage = ""
    }

    fun validateTitle(): String? {
        return if (title.isBlank()) "Title can't be blank." else null
    }

    fun validateDetails(): String? {
        return if (details.length < 5) "Details must be at least 5 characters." else null
    }

    fun validateAmount(): String? {
        val value = amount.toDoubleOrNull()
        return when {
            value == null -> "Enter a valid number."
            value <= 0.0 -> "Amount must be greater than 0."
            else -> null
        }
    }

    fun validateCategory(): String? {
        return if (selectedCategory == "Select Category") "Please select a category." else null
    }

    fun validateAccount(): String? {
        return if (selectedAccount == "Select Category") "Please select an account." else null
    }

    fun validateDate(): String? {
        return if (selectedDate == null) "Please select a valid date." else null
    }

    // Form validation
    fun validateForm(): Boolean {
        titleError = validateTitle()
        amountError = validateAmount()
        detailsError = validateDetails()
        categoryError = validateCategory()
        accountError = validateAccount()
//        dateError = validateDate()

        val anyBlank = listOf(title, amount, details).any {
            it.isBlank()
        }

        val errors = mutableListOf<String>()
        titleError?.let { errors.add(it) }
        amountError?.let { errors.add(it) }
        detailsError?.let { errors.add(it) }
        categoryError?.let { errors.add(it) }
        accountError?.let { errors.add(it) }
//        dateError?.let { errors.add(it) }

        formErrorMessage = when {
            anyBlank -> "Please, fill all the blank fields."
            errors.isNotEmpty() -> errors.first()
            else -> null
        }
        // Return true if all errors are null
        return !anyBlank && errors.isEmpty()
    }

    fun deleteIncome(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeRepo.deleteAnIncome(id)
        }
    }

}
