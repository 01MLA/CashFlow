package com.example.cashflow.presentation.ui.transactions

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.cashflow.CashFlowApplication
import com.example.cashflow.data.repository.AccountRepository
import com.example.cashflow.data.repository.CategoryRepository
import com.example.cashflow.data.repository.ExpenseRepository
import com.example.cashflow.domain.model.Account
import com.example.cashflow.domain.model.Category
import com.example.cashflow.domain.model.Expense
import com.example.cashflow.presentation.ui.home.model.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val expenseRepo = ExpenseRepository(database.expenseDao())
    private val categoryRepo = CategoryRepository(database.categoryDao())
    private val accountRepo = AccountRepository(database.accountDao())

    // these two are declared here because used by more then one screens
    var showNewExpenseSheet by mutableStateOf(false)
    var showEditExpenseSheet by mutableStateOf(false)

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // UiState for expenses
    private val _expenses = mutableStateOf<UiState<List<Expense>>>(UiState.Loading)
    val expenses: State<UiState<List<Expense>>> = _expenses

    private var recentlyDeletedExpense: Expense? = null

    // Accounts state
    private val _accounts = mutableStateOf<List<Account>>(emptyList())
    val accounts: State<List<Account>> = _accounts

    // Categories state
    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories

    // Total Expenses
    private val _totalExpense = mutableDoubleStateOf(0.0)
    val totalExpenses: State<Double> = _totalExpense

    init {
        loadAccounts()
        loadCategories()
        loadExpenses()
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

    // Add expense to database
    fun addExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                expenseRepo.addExpense(expense)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun exportMyExpenses() {
        viewModelScope.launch {
            try {
//                exportIncomesToExcel(application, expenseRepo.getExpenses().first())
                Toast.makeText(
                    application, "Expenses were exported successfully!", Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(application, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                expenseRepo.updateExpense(expense)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Remove expense visually and show undo option
    fun deleteExpense(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Get current list from UiState or return if unavailable
            val currList = (expenses.value as? UiState.Success)?.data ?: return@launch

            // Keep the deleted income for possible undo
            recentlyDeletedExpense = expenseRepo.getAnExpenseById(id).first()

            // Create new list without the deleted expense
            val updatedList = currList.filter { it.id != id }

            // Update UI state with the new list
            _expenses.value = UiState.Success(updatedList)

            // Trigger snackbar message to show deletion success
            _snackbarMessage.emit("Expense deleted successfully!")
        }
    }

    // Permanently delete expense from database after confirmation
    fun confirmDelete() {
        recentlyDeletedExpense?.let { income ->
            viewModelScope.launch {
                try {
                    expenseRepo.deleteExpense(income.id) // Delete expense from repository (DB)
                    recentlyDeletedExpense = null // Clear reference since it's permanently deleted
                    loadExpenses() // Reload expenses to refresh UI with updated data
                } catch (e: Exception) {
                    e.printStackTrace() // Log any errors during delete operation
                }
            }
        }
    }

    // Restore previously deleted expense (undo action)
    fun undoDelete() {
        recentlyDeletedExpense?.let { expense ->
            viewModelScope.launch {
                // Get current list from UiState or use empty list
                val currList = (expenses.value as? UiState.Success)?.data ?: emptyList()

                // Fetch the deleted expense again for restoration
                recentlyDeletedExpense = expenseRepo.getAnExpenseById(expense.id).first()

                // Rebuild list by re-adding deleted expense (visual restore)
                val updatedList = currList + recentlyDeletedExpense!!

                // Update UI state to show restored expense
                _expenses.value = UiState.Success(updatedList)

                // Clear reference since undo is complete
                recentlyDeletedExpense = null
            }
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
    fun loadExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            _expenses.value = UiState.Loading
            _totalExpense.doubleValue = 0.0
            delay(1200)
            try {
                val result = expenseRepo.getExpenses().first()
                result.forEach {
                    _totalExpense.doubleValue += it.amount
                }
                _expenses.value = UiState.Success(result)
            } catch (e: Exception) {
                _expenses.value = UiState.Error(e, e.message ?: "Unknown error")
            }
        }
    }

    suspend fun getExpense(id: Int): Expense? {
        return try {
            expenseRepo.getAnExpenseById(id).firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // get A category title
    fun getCategory(id: Int) = _categories.value.find { it.id == id }
    fun getAccount(id: Int) = _accounts.value.find { it.id == id }

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
}
