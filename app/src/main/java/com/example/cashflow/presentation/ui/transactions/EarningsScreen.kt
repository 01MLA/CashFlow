package com.example.cashflow.presentation.ui.transactions

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashflow.R
import com.example.cashflow.domain.model.Income
import com.example.cashflow.domain.model.TransactionType
import com.example.cashflow.presentation.components.CashFlowConfirmDialog
import com.example.cashflow.presentation.components.TransactionDetailsPopup
import com.example.cashflow.presentation.ui.home.components.CashFlowItem
import com.example.cashflow.presentation.ui.home.components.CashFlowItemShimmer
import com.example.cashflow.presentation.ui.home.components.ItemModel
import com.example.cashflow.presentation.ui.home.model.UiState
import com.example.cashflow.presentation.util.EarningsMenuAction
import com.example.cashflow.util.formatMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarningsScreen(innerPaddings: PaddingValues, viewModel: EarningsViewModel) {
    val context = LocalContext.current
    val selectedItems = remember { mutableStateListOf<Int>() }
    val incomes: UiState<List<Income>> by viewModel.incomes
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var transactionToShowDetails by remember { mutableIntStateOf(-1) }
    var incomeToEdit by remember { mutableIntStateOf(-1) }
    var incomeToDelete by remember { mutableIntStateOf(-1) }
    var earningDropDownExpanded by remember { mutableStateOf(false) }
    val importState = viewModel.importState.collectAsState()

    val saveFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri ->
        if (uri != null) {
            viewModel.getEarningsForExport(onResult = { earnings, categories, accounts ->
                viewModel.viewModelScope.launch(Dispatchers.IO) {
                    viewModel.exportEarningsToExcel(context, uri, earnings, categories, accounts)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context, "Earnings exported successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }, onError = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            })
        }
    }

    val importExcelLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) viewModel.importEarningsFromExcel(context, uri)
        }

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
            onClick = { viewModel.showNewIncomeSheet = !viewModel.showNewIncomeSheet }) {
            Icon(imageVector = Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
        }
    }, snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.earnings),
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(
                        R.string.total, formatMoney(
                            viewModel.totalIncomes.value, stringResource(R.string.currency)
                        )
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(end = 6.dp)
                )

                IconButton(
                    onClick = { earningDropDownExpanded = true }, modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "export earnings button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Box {// Used to align the dropdown to top-right:
                    // DropdownMenu always opens relative to its immediate parent — so give it the right parent.
                    val items = remember { // remember to not create on recomposition
                        listOf(
                            EarningsMenuAction.Import,
                            EarningsMenuAction.Export,
                            EarningsMenuAction.Delete
                        )
                    }
                    DropdownMenu(
                        expanded = earningDropDownExpanded,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = 0.1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        onDismissRequest = { earningDropDownExpanded = false }) {
                        items.forEach { item ->
                            DropdownMenuItem(leadingIcon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }, text = { Text(item.title) }, onClick = {
                                earningDropDownExpanded = false
                                when (item) {
                                    EarningsMenuAction.Import -> {
                                        importExcelLauncher.launch(
                                            arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                        )
                                    }

                                    EarningsMenuAction.Export -> {
                                        val timeStamp = SimpleDateFormat(
                                            "yyyy-MM-dd_HH-mm", Locale.getDefault()
                                        ).format(Date())
                                        saveFileLauncher.launch("Earnings_$timeStamp.xlsx")
                                    }

                                    EarningsMenuAction.Delete -> viewModel.deleteAllEarnings()
                                }
                            })
                        }
                    }
                }
            }

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            when (incomes) {
                is UiState.Idle -> {}
                is UiState.Loading -> {
                    val itemHeight = 100.dp // Approximate height of each item
                    // Estimate number of items to fill screen
                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    val count = (screenHeight / itemHeight).toInt() + 2 // add a bit extra

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(count) { // show n shimmer placeholders
                            CashFlowItemShimmer()
                        }
                    }
                }

                is UiState.Success -> {
                    val theIncomes = (incomes as UiState.Success).data
                    if (theIncomes.isEmpty()) {
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
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {// Selected-Items Delete Button
                                Column {
                                    if (!selectedItems.isEmpty()) {
                                        AnimatedVisibility(
                                            visible = true, enter = fadeIn(), exit = fadeOut()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 0.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                OutlinedButton(onClick = {
                                                    viewModel.deleteEarnings(selectedItems)
                                                    selectedItems.clear()
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

                            items(theIncomes) { income ->
                                CashFlowItem(
                                    onClick = {
                                        if (selectedItems.isEmpty()) transactionToShowDetails =
                                            income.id
                                        else if (!selectedItems.contains(income.id)) selectedItems += income.id
                                        else selectedItems -= income.id
                                    },
                                    onLongClick = {
                                        if (!selectedItems.contains(income.id)) selectedItems += income.id
                                    },
                                    item = ItemModel(
                                        title = income.title,
                                        details = income.details,
                                        amount = income.amount,
                                        categoryId = income.categoryId,
                                        date = income.date
                                    ),
                                    isSelected = selectedItems.contains(income.id),
                                    onEditClicked = {
                                        incomeToEdit = income.id
                                        viewModel.showEditIncomeSheet = true
                                    },
                                    onDeleteClicked = {
                                        incomeToDelete = income.id
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
                        val state = (incomes as UiState.Error)
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

            if (viewModel.showNewIncomeSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxSize(), sheetState = sheetState, onDismissRequest = {
                        viewModel.resetForm()
                        viewModel.showNewIncomeSheet = !viewModel.showNewIncomeSheet
                    }) {
                    val viewModel: EarningsViewModel = viewModel()
                    if (viewModel.categories.value.isEmpty() || viewModel.accounts.value.isEmpty()) NewEarningScreenShimmer() // not usable now
                    else NewEarningScreen({
                        viewModel.showNewIncomeSheet = !viewModel.showNewIncomeSheet
                    }, viewModel)
                }
            }
            if (viewModel.showEditIncomeSheet) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxSize(), sheetState = sheetState, onDismissRequest = {
                        viewModel.resetForm()
                        viewModel.showEditIncomeSheet = !viewModel.showEditIncomeSheet
                    }) {
                    val viewModel: EarningsViewModel = viewModel()
                    if (viewModel.categories.value.isEmpty() || viewModel.accounts.value.isEmpty()) EditEarningScreenShimmer() // not usable now
                    else EditEarningScreen(id = incomeToEdit) {
                        viewModel.showEditIncomeSheet = !viewModel.showEditIncomeSheet
                    }
                }
            }
        }
    }

    if (transactionToShowDetails != -1) {
        TransactionDetailsPopup(
            transactionToShowDetails,
            onDismissRequest = { transactionToShowDetails = -1 },
            transactionType = TransactionType.INCOME
        )
    }
    if (showDeleteConfirmDialog && incomeToDelete != -1) {
        CashFlowConfirmDialog(
            onConfirm = {
                val income =
                    (incomes as? UiState.Success<List<Income>>)?.data?.find { it.id == incomeToDelete }
                if (income != null) viewModel.deleteEarning(incomeToDelete)
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false },
            title = stringResource(R.string.delete_income),
            message = stringResource(R.string.are_you_sure_to_delete_this_income),
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
