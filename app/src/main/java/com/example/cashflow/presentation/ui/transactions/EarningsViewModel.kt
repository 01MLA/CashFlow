package com.example.cashflow.presentation.ui.transactions

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.example.cashflow.data.excel.formatDate
import com.example.cashflow.data.repository.AccountRepository
import com.example.cashflow.data.repository.CategoryRepository
import com.example.cashflow.data.repository.IncomeRepository
import com.example.cashflow.domain.model.Account
import com.example.cashflow.domain.model.Category
import com.example.cashflow.domain.model.Income
import com.example.cashflow.presentation.ui.home.model.LoadingUIState
import com.example.cashflow.presentation.ui.home.model.UiState
import com.example.cashflow.presentation.util.getDate
import com.example.cashflow.presentation.util.getDouble
import com.example.cashflow.presentation.util.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate

class EarningsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as CashFlowApplication).database
    private val incomeRepo = IncomeRepository(database.incomeDao())
    private val categoryRepo = CategoryRepository(database.categoryDao())
    private val accountRepo = AccountRepository(database.accountDao())

    private val _importState = MutableStateFlow<LoadingUIState<Unit>>(LoadingUIState.Idle)
    val importState = _importState.asStateFlow()

    private val _exportState = MutableStateFlow<LoadingUIState<Unit>>(LoadingUIState.Idle)
    val exportState = _exportState.asStateFlow()

    // these two are declared here because used by more then one screens
    var showNewIncomeSheet by mutableStateOf(false)
    var showEditIncomeSheet by mutableStateOf(false)

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // UiState for incomes
    private val _incomes = mutableStateOf<UiState<List<Income>>>(UiState.Loading)
    val incomes: State<UiState<List<Income>>> = _incomes

    private var recentlyDeletedIncome: Income? = null

    // Accounts state
    private val _accounts = mutableStateOf<List<Account>>(emptyList())
    val accounts: State<List<Account>> = _accounts

    // Categories state
    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories: State<List<Category>> = _categories

    // Total Incomes
    private val _totalIncome = mutableDoubleStateOf(0.0)
    val totalIncomes: State<Double> = _totalIncome

    init {
        loadAccounts()
        loadCategories()
        loadIncomes()
    }

    var showDateDialog by mutableStateOf(false)

    // Form fields
    var title by mutableStateOf("")
    var amount by mutableStateOf("")
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
            try {
                incomeRepo.addIncome(income)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importEarningsFromExcel(context: Context, uri: Uri) {
        viewModelScope.launch {
            _importState.value = LoadingUIState.Loading
            val earnings = mutableListOf<Income>()
            val categoryCache = mutableMapOf<String, Int>()
            val accountCache = mutableMapOf<String, Int>()
            try {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val workbook = XSSFWorkbook(inputStream)
                        val sheet = workbook.getSheetAt(0)
                        sheet.drop(1).forEach { row ->
                            val id = row.getCell(0).getString().toIntOrNull() ?: 0
                            val title = row.getCell(1).getString()
                            val details = row.getCell(2).getString()
                            val amount = row.getCell(3).getDouble()
                            val categoryName = row.getCell(4).getString()
                            val accountName = row.getCell(5).getString()
                            val date = row.getCell(6).getDate()?.let { formatDate(it) } ?: ""

                            val categoryId = categoryCache.getOrPut(categoryName) {
                                getOrCreateCategoryId(categoryName)
                            }
                            val accountId = accountCache.getOrPut(accountName) {
                                getOrCreateAccountId(accountName)
                            }

                            earnings.add(
                                Income(
                                    id = id,
                                    title = title,
                                    details = details,
                                    amount = amount,
                                    categoryId = categoryId,
                                    accountId = accountId,
                                    date = date
                                )
                            )
                        }
                        workbook.close()
                    }
                    saveImportedEarnings(earnings)
                }
                loadIncomes()
                _importState.value = LoadingUIState.Success(Unit)
            } catch (e: Exception) {
                Log.e("logs", "excel import failed", e)
                _importState.value = LoadingUIState.Error(e, message = "Excel import failed")
            }
        }
    }

    suspend fun saveImportedEarnings(earnings: List<Income>) {
        try {
            incomeRepo.addIncomes(earnings)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    application,
                    "${earnings.size} earning items saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.d("logs", "${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(application, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun getOrCreateCategoryId(category: String): Int {
        return try {
            // 1. Check if category exists
            val existingId = categoryRepo.getCategoryId(category)
            if (existingId != null) return existingId

            // 2. Insert new category
            val newCategory = Category(title = category)
            categoryRepo.addCategory(newCategory) // returns row count

            // 3. Query again to get the ID
            categoryRepo.getCategoryId(category)
                ?: throw IllegalStateException("Failed to get ID for newly inserted category '$category'")
        } catch (e: Exception) {
            Log.d("logs", "Error in getOrCreateCategoryId: ${e.message}")
            -1
        }
    }

    suspend fun getOrCreateAccountId(account: String): Int {
        return try {
            // 1. Check if account exists
            val existedId = accountRepo.getAccountId(account)
            if (existedId != null) return existedId

            // 2. Insert new account
            val newAccount = Account(title = account, balance = 0.0)
            accountRepo.addAccount(newAccount)

            // 3️. Query again to get the ID
            accountRepo.getAccountId(account)
                ?: throw IllegalStateException("Failed to get ID for newly inserted account '$account'")
        } catch (e: Exception) {
            Log.d("logs", "Error in getOrCreateAccountId: ${e.message}")
            -1
        }
    }

    fun getEarningsForExport(
        onResult: (List<Income>, Map<Int, String>, Map<Int, String>) -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                val earnings = incomeRepo.getIncomes().first()
                val categories = mutableMapOf<Int, String>()
                val accounts = mutableMapOf<Int, String>()
                earnings.forEach {
                    categories[it.id] = getCategory(it.categoryId)?.title.orEmpty()
                    accounts[it.id] = getAccount(it.accountId)?.title.orEmpty()
                }
                onResult(earnings, categories, accounts)
            } catch (e: Exception) {
                Log.d("logs", "${e.message}")
                onError(e.message ?: "Export failed")
            }
        }
    }

    fun exportEarningsToExcel(
        context: Context,
        uri: Uri,
        earnings: List<Income>,
        categories: Map<Int, String>,
        accounts: Map<Int, String>,
    ) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Earnings")

        // Header
        val headerRow = sheet.createRow(0)
        val headers = listOf("Id", "Title", "Details", "Amount", "Category", "Account", "Date")
        headers.forEachIndexed { index, colTitle ->
            headerRow.createCell(index).setCellValue(colTitle)
        }

        // DateStyle
        val dateCellStyle = workbook.createCellStyle()
        dateCellStyle.dataFormat =
            workbook.creationHelper.createDataFormat().getFormat("yyyy-MM-dd")

        // Rows
        earnings.forEachIndexed { index, income ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(income.id.toString())
            row.createCell(1).setCellValue(income.title)
            row.createCell(2).setCellValue(income.details)
            row.createCell(3).setCellValue(income.amount)
            row.createCell(4).setCellValue(categories[income.id].orEmpty())
            row.createCell(5).setCellValue(accounts[income.id].orEmpty())

            val dateCell = row.createCell(6)
            dateCell.cellStyle = dateCellStyle
            dateCell.setCellValue(income.date)
        }

        // Set column widths
        sheet.setColumnWidth(0, 5 * 256) // ID
        sheet.setColumnWidth(1, 25 * 256) // Title
        sheet.setColumnWidth(2, 35 * 256) // Details
        sheet.setColumnWidth(3, 10 * 256) // Amount
        sheet.setColumnWidth(4, 20 * 256) // Category
        sheet.setColumnWidth(5, 20 * 256) // Account
        sheet.setColumnWidth(6, 15 * 256) // Date

        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            workbook.write(outputStream)
        }
        workbook.close()
    }

    fun updateIncome(income: Income) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                incomeRepo.updateIncome(income)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Remove income visually and show undo option
    fun deleteEarning(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Get current list from UiState or return if unavailable
            val currList = (incomes.value as? UiState.Success)?.data ?: return@launch

            // Keep the deleted income for possible undo
            recentlyDeletedIncome = incomeRepo.getAnIncomeById(id).first()

            // Create new list without the deleted income
            val updatedList = currList.filter { it.id != id }

            // Update UI state with the new list
            _incomes.value = UiState.Success(updatedList)

            // Trigger snackbar message to show deletion success
            _snackbarMessage.emit("Expense deleted successfully!")
        }
    }

    // Permanently delete income from database after confirmation
    fun confirmDelete() {
        recentlyDeletedIncome?.let { income ->
            viewModelScope.launch {
                try {
                    incomeRepo.deleteAnIncome(income.id) // Delete income from repository (DB)
                    recentlyDeletedIncome = null // Clear reference since it's permanently deleted
                    loadIncomes() // Reload incomes to refresh UI with updated data
                } catch (e: Exception) {
                    e.printStackTrace() // Log any errors during delete operation
                }
            }
        }
    }

    fun clearIncomesState() {
        _incomes.value = UiState.Success(emptyList())
    }

    // To delete a group of earnings at once
    fun deleteEarnings(ids: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                incomeRepo.deleteIncomes(ids) // Delete from DB
                withContext(Dispatchers.Main) {
                    clearIncomesState()
                    Toast.makeText(
                        application,
                        "All selected items have been deleted successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                loadIncomes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAllEarnings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                incomeRepo.deleteAllIncomes()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        application, "All items have been deleted successfully!", Toast.LENGTH_LONG
                    ).show()
                }
                loadIncomes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Restore previously deleted income (undo action)
    fun undoDelete() {
        recentlyDeletedIncome?.let { income ->
            viewModelScope.launch {
                // Get current list from UiState or use empty list
                val currList = (incomes.value as? UiState.Success)?.data ?: emptyList()

                // Fetch the deleted income again for restoration
                recentlyDeletedIncome = incomeRepo.getAnIncomeById(income.id).first()

                // Rebuild list by re-adding deleted income (visual restore)
                val updatedList = currList + recentlyDeletedIncome!!

                // Update UI state to show restored income
                _incomes.value = UiState.Success(updatedList)

                // Clear reference since undo is complete
                recentlyDeletedIncome = null
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
    fun loadIncomes() {
        viewModelScope.launch(Dispatchers.IO) {
            _incomes.value = UiState.Loading
            _totalIncome.doubleValue = 0.0
            delay(1200)
            try {
                val result = incomeRepo.getIncomes().first()
                result.forEach {
                    _totalIncome.doubleValue += it.amount
                }
                _incomes.value = UiState.Success(result)
            } catch (e: Exception) {
                _incomes.value = UiState.Error(e, e.message ?: "Unknown error")
            }
        }
    }

    suspend fun getIncome(id: Int): Income? {
        return try {
            incomeRepo.getAnIncomeById(id).firstOrNull()
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
