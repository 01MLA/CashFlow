package com.example.cashflow.presentation.ui.transactions

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashflow.R
import com.example.cashflow.presentation.ui.home.components.CashFlowItem
import com.example.cashflow.presentation.ui.home.components.CashFlowItemShimmer
import com.example.cashflow.presentation.ui.home.components.ItemModel
import com.example.cashflow.presentation.ui.home.model.UiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EarningsScreen(innerPaddings: PaddingValues, viewModel: EarningsViewModel) {
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf<List<String>>(emptyList()) }
    val incomes by viewModel.incomes

    Scaffold(modifier = Modifier.padding(innerPaddings), floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.padding(end = 10.dp), onClick = { showSheet = !showSheet }) {
            Icon(imageVector = Icons.Default.Add, null)
        }
    }) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        when (incomes) {
            is UiState.Idle -> {}
            is UiState.Loading -> {
                val itemHeight = 100.dp // Approximate height of each item
                // Estimate number of items to fill screen
                val screenHeight =
                    androidx.compose.ui.platform.LocalConfiguration.current.screenHeightDp.dp
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(theIncomes) { income ->
                            CashFlowItem(
                                modifier = Modifier.combinedClickable(onClick = {}, onLongClick = {
                                    if (selectedItems.contains(income.id.toString())) {
                                        selectedItems -= income.id.toString()
                                    } else {
                                        selectedItems += income.id.toString()
                                    }
                                }), item = ItemModel(
                                    title = income.title,
                                    details = income.details,
                                    amount = income.amount,
                                    categoryId = income.categoryId,
                                    date = income.date
                                ), onDelete = {
                                    selectedItems += income.id.toString()
                                    showDeleteConfirmDialog = !showDeleteConfirmDialog
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

        if (showSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(), sheetState = sheetState, onDismissRequest = {
                    viewModel.resetForm()
                    showSheet = !showSheet
                }) {
                val viewModel: EarningsViewModel = viewModel()
                if (viewModel.categories.value.isEmpty() || viewModel.accounts.value.isEmpty()) NewEarningScreenShimmer() // not usable now
                else NewEarningScreen({ showSheet = !showSheet }, viewModel)
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Are you sure you want to delete this item?",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDeleteConfirmDialog = false }) {
                                Text("Cancel")
                            }
                            TextButton(onClick = {
                                selectedItems.forEach { viewModel.deleteIncome(it) }
                                selectedItems = emptyList()
                                showDeleteConfirmDialog = false
                                viewModel.loadIncomes()
                            }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
