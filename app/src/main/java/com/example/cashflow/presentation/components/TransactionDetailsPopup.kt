package com.example.cashflow.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashflow.R
import com.example.cashflow.domain.model.Transaction
import com.example.cashflow.domain.model.TransactionType
import com.example.cashflow.presentation.ui.transactions.EarningsViewModel
import com.example.cashflow.presentation.ui.transactions.ExpensesViewModel
import com.example.cashflow.util.formatMoney

@Composable
fun TransactionDetailsPopup(
    transactionId: Int,
    transactionType: TransactionType,
    onDismissRequest: () -> Unit,
) {
    val earningsViewModel: EarningsViewModel = viewModel()
    val expensesViewModel: ExpensesViewModel = viewModel()
    var transaction by remember { mutableStateOf<Transaction?>(null) }

    // Fetch transaction details
    LaunchedEffect(Unit) {
        transaction =
            if (transactionType == TransactionType.INCOME) earningsViewModel.getIncome(transactionId)
            else expensesViewModel.getExpense(transactionId)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 200.dp, max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            if (transaction == null) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Loading details...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Transaction Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )

                    DetailRow("Title", transaction!!.title)
                    DetailRow(
                        "Amount", formatMoney(
                            transaction!!.amount, stringResource(R.string.currency)
                        )
                    )
                    DetailRow("Details", transaction!!.details.ifBlank { "—" })
                    DetailRow(
                        "Category: ",
                        earningsViewModel.getCategory(transaction!!.categoryId)?.title ?: ""
                    )
                    DetailRow(
                        "Account: ",
                        earningsViewModel.getAccount(transaction!!.accountId)?.title ?: ""
                    )

                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Ok") }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Text(
            modifier = Modifier.padding(end = 12.dp),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
