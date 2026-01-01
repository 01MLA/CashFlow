package com.example.cashflow.presentation.ui.transactions

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashflow.R
import com.example.cashflow.domain.model.Expense
import com.example.cashflow.domain.model.TransactionType
import com.example.cashflow.presentation.components.CashFlowConfirmDialog
import com.example.cashflow.presentation.components.TransactionDetailsPopup
import com.example.cashflow.presentation.ui.home.components.CashFlowItem
import com.example.cashflow.presentation.ui.home.components.CashFlowItemShimmer
import com.example.cashflow.presentation.ui.home.components.ItemModel
import com.example.cashflow.presentation.ui.home.model.UiState
import com.example.cashflow.util.formatMoney
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ConfigurationScreenWidthHeight")
@Composable
fun ExpensesScreen(innerPaddings: PaddingValues, viewModel: ExpensesViewModel) {
    val context = LocalContext.current
    var selectedItems by remember { mutableStateOf<List<Int>>(emptyList()) }
    val expenses: UiState<List<Expense>> by viewModel.expenses
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var transactionToShowDetails by remember { mutableIntStateOf(-1) }
    var expenseToEdit by remember { mutableIntStateOf(-1) }
    var expenseToDelete by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            val result = snackbarHostState.showSnackbar(
                message = message, actionLabel = "Undo", duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> viewModel.undoDelete()
                SnackbarResult.Dismissed -> viewModel.confirmDelete()
            }
        }
    }

    Scaffold(modifier = Modifier.padding(innerPaddings), floatingActionButton = {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 10.dp),
            onClick = { viewModel.showNewExpenseSheet = !viewModel.showNewExpenseSheet }) {
            Icon(imageVector = Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
        }
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.earnings),
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
//                Text(
//                    text = stringResource(
//                        R.string.total, formatMoney(
//                            viewModel.totalIncomes.value, stringResource(R.string.currency)
//                        )
//                    ),
//                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
//                    fontWeight = FontWeight.SemiBold,
//                    color = MaterialTheme.colorScheme.outline,
//                    modifier = Modifier.padding(12.dp, 8.dp)
//                )
//                IconButton(onClick = {
//                    val timeStamp = SimpleDateFormat(
//                        "yyyy-MM-dd_HH-mm", Locale.getDefault()
//                    ).format(Date())
//                    saveFileLauncher.launch("earnings_$timeStamp.xlsx")
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.Save,
//                        contentDescription = "export earnings button",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//                IconButton(onClick = {
//                    importExcelLauncher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                }) {
//                    Icon(
//                        imageVector = Icons.Default.InsertChart,
//                        contentDescription = "import earnings button",
//                        tint = Color.Green
//
//                    )
//                }
            }

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            when (expenses) {
                is UiState.Idle -> {}
                is UiState.Loading -> {
                    val itemHeight = 100.dp // Approximate height of each item
                    // Estimate number of items to fill screen
                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    val count = (screenHeight / itemHeight).toInt() + 2 // add a bit extra

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(count) { // show n shimmer placeholders
                            CashFlowItemShimmer()
                        }
                    }
                }

                is UiState.Success -> {
                    val theExpenses = (expenses as UiState.Success).data
                    if (theExpenses.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = stringResource(R.string.no_data_available),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            stringResource(R.string.expenses),
                                            modifier = Modifier.padding(start = 10.dp),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 17.sp
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            text = stringResource(
                                                R.string.total, formatMoney(
                                                    viewModel.totalExpenses.value,
                                                    stringResource(R.string.currency)
                                                )
                                            ),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 16.sp
                                            ),
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.padding(12.dp, 8.dp)
                                        )
                                        Row {
                                            IconButton(onClick = { viewModel.exportMyExpenses() }) {
                                                Icon(
                                                    imageVector = Icons.Default.Save,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(onClick = { }) {
                                                Icon(
                                                    imageVector = Icons.Default.InsertChart,
                                                    contentDescription = null,
                                                    tint = Color.Green
                                                )
                                            }
                                        }
                                    }

                                    if (!selectedItems.isEmpty()) {
                                        AnimatedVisibility(
                                            visible = true, enter = fadeIn(), exit = fadeOut()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                OutlinedButton(onClick = {
                                                    selectedItems.forEach {
                                                        viewModel.deleteExpense(it)
                                                        selectedItems -= it
                                                    }
                                                    Toast.makeText(
                                                        context,
                                                        "All selected items has been deleted successfully!",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    viewModel.loadExpenses()
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.DeleteOutline,
                                                        tint = MaterialTheme.colorScheme.error,
                                                        contentDescription = null
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    Text(
                                                        "Delete Items",
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(Modifier.width(12.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .border(
                                                                1.dp,
                                                                color = MaterialTheme.colorScheme.primary,
                                                                shape = CircleShape
                                                            ), contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "${selectedItems.size}",
                                                            style = MaterialTheme.typography.bodySmall.copy(
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 14.sp
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            items(theExpenses) { expense ->
                                CashFlowItem(
                                    onClick = {
                                    if (selectedItems.isEmpty()) transactionToShowDetails =
                                        expense.id
                                    else if (!selectedItems.contains(expense.id)) selectedItems += expense.id
                                    else selectedItems -= expense.id
                                },
                                    onLongClick = {
                                        if (!selectedItems.contains(expense.id)) selectedItems += expense.id
                                    },
                                    item = ItemModel(
                                        title = expense.title,
                                        details = expense.details,
                                        amount = expense.amount,
                                        categoryId = expense.categoryId,
                                        date = expense.date
                                    ),
                                    isSelected = selectedItems.contains(expense.id),
                                    onEditClicked = {
                                        expenseToEdit = expense.id
                                        viewModel.showEditExpenseSheet = true
                                    },
                                    onDeleteClicked = {
                                        expenseToDelete = expense.id
                                        showDeleteConfirmDialog = true
                                    })
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        val state = (expenses as UiState.Error)
                        Text(
                            text = state.message,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Log.d("logs", state.message)
                    }
                }
            }

            if (viewModel.showNewExpenseSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxSize(), sheetState = sheetState, onDismissRequest = {
                        viewModel.resetForm()
                        viewModel.showNewExpenseSheet = !viewModel.showNewExpenseSheet
                    }) {
                    val viewModel: ExpensesViewModel = viewModel()
                    if (viewModel.categories.value.isEmpty() || viewModel.accounts.value.isEmpty()) NewExpenseScreenShimmer() // not usable now
                    else NewExpenseScreen({
                        viewModel.showNewExpenseSheet = !viewModel.showNewExpenseSheet
                    }, viewModel)
                }
            }
            if (viewModel.showEditExpenseSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxSize(), sheetState = sheetState, onDismissRequest = {
                        viewModel.resetForm()
                        viewModel.showEditExpenseSheet = !viewModel.showEditExpenseSheet
                    }) {
                    val viewModel: ExpensesViewModel = viewModel()
                    if (viewModel.categories.value.isEmpty() || viewModel.accounts.value.isEmpty()) EditExpenseScreenShimmer() // not usable now
                    else EditExpenseScreen(id = expenseToEdit) {
                        viewModel.showEditExpenseSheet = !viewModel.showEditExpenseSheet
                    }
                }
            }
        }
    }

    if (transactionToShowDetails != -1) {
        TransactionDetailsPopup(
            transactionToShowDetails,
            onDismissRequest = { transactionToShowDetails = -1 },
            transactionType = TransactionType.EXPENSE
        )
    }
    if (showDeleteConfirmDialog && expenseToDelete != -1) {
        CashFlowConfirmDialog(
            onConfirm = {
                val expense =
                    (expenses as? UiState.Success<List<Expense>>)?.data?.find { it.id == expenseToDelete }
                if (expense != null) viewModel.deleteExpense(expenseToDelete)
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false },
            title = stringResource(R.string.delete_expense),
            message = stringResource(R.string.are_you_sure_to_delete_this_expense),
            iconImage = {
                Image(
                    modifier = it,
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null
                )
            },
        )
    }
}
