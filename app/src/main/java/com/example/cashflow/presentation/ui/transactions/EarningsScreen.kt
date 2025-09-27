package com.example.cashflow.presentation.ui.transactions

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashflow.R
import com.example.cashflow.presentation.ui.home.components.CashFlowItem
import com.example.cashflow.presentation.ui.home.components.ItemModel
import com.example.cashflow.presentation.ui.home.model.UiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EarningsScreen(innerPaddings: PaddingValues, viewModel: EarningsViewModel) {
    var showSheet by remember { mutableStateOf(false) }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 0.4.dp,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(18.dp)
                    )
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
                                item = ItemModel(
                                    title = income.title,
                                    details = income.details,
                                    amount = income.amount,
                                    category = income.categoryId,
                                    date = income.date
                                )
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                val state = (incomes as UiState.Error)
                Text(
                    text = state.message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                sheetState = sheetState,
                onDismissRequest = { showSheet = !showSheet }) {
                val viewModel: EarningsViewModel = viewModel()
                NewEarningScreen({ showSheet = !showSheet }, viewModel)
            }
        }

    }
}
