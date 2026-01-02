package com.example.cashflow.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.extensions.isNotNull
import com.example.cashflow.R
import com.example.cashflow.domain.model.Expense
import com.example.cashflow.domain.model.Income
import com.example.cashflow.domain.model.Transaction
import com.example.cashflow.domain.model.TransactionType
import com.example.cashflow.presentation.components.TransactionDetailsPopup
import com.example.cashflow.presentation.ui.home.components.CashFlowRecentItem
import com.example.cashflow.presentation.ui.home.components.ItemModel
import com.example.cashflow.util.formatMoney

@Composable
fun HomeDashboard(incomes: List<Income>, expenses: List<Expense>) {
    val totalIncome = incomes.sumOf { it.amount }
    val totalExpense = expenses.sumOf { it.amount }
    val balance = (totalIncome - totalExpense)
    val transactions: List<Transaction> =
        (incomes + expenses).map { it }.sortedByDescending { it.date }
    var transactionToShowDetails: TransactionToShowDetails? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 88.dp, bottom = 70.dp) // top padding if you have TopBar or similar
    ) {
        // Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.cashflow_balance),
                    fontFamily = FontFamily.Cursive,
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatMoney(balance, stringResource(R.string.currency)),
                    style = MaterialTheme.typography.displayMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(3f, 3f),
                            blurRadius = 4f
                        )
                    ),
                    fontFamily = FontFamily.Cursive,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Summary cards ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.earnings),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Cursive
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatMoney(totalIncome, stringResource(R.string.currency)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32) // green
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.expenses),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Cursive
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatMoney(totalExpense, stringResource(R.string.currency)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFC62828) // red
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recent transactions
        Column(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    0.1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .background(Color(0xFFFFF3E0))
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Text(
                stringResource(R.string.recent_transactions),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Transaction List ---
            transactions.take(10).forEach { item ->
                CashFlowRecentItem(
                    item = ItemModel(
                        title = item.title,
                        details = item.details,
                        amount = item.amount,
                        categoryId = item.categoryId,
                        date = item.date
                    ), type = item.type, onClick = {
                        transactionToShowDetails = TransactionToShowDetails(item.id, item.type)
                    })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (transactionToShowDetails.isNotNull()) {
        TransactionDetailsPopup(
            transactionToShowDetails?.transactionId!!,
            onDismissRequest = { transactionToShowDetails = null },
            transactionType = transactionToShowDetails!!.transactionType
        )
    }
}

data class TransactionToShowDetails(
    val transactionId: Int,
    val transactionType: TransactionType,
)
