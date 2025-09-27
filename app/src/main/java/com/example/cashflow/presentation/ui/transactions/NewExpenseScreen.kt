package com.example.cashflow.presentation.ui.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashflow.R
import com.example.cashflow.domain.model.Outcome
import com.example.cashflow.presentation.components.CashFlowButton
import com.example.cashflow.presentation.components.CashFlowTextField
import com.example.cashflow.presentation.components.OutlinedCashFlowButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewExpenseScreen(onCancelClicked: () -> Unit, viewModel: ExpensesViewModel) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = viewModel.selectedDate?.atStartOfDay(ZoneId.systemDefault())
            ?.toInstant()?.toEpochMilli()
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(painter = painterResource(R.drawable.app_logo), contentDescription = null)
        Text(
            "Add new income.",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
        CashFlowTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Title") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { Text("e.g. Salary", color = MaterialTheme.colorScheme.outline) },
        )
        CashFlowTextField(
            value = viewModel.details,
            onValueChange = { viewModel.details = it },
            label = { Text("Details") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = {
                Text(
                    "e.g. January salary", color = MaterialTheme.colorScheme.outline
                )
            },
        )
        CashFlowTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("e.g. 12000", color = MaterialTheme.colorScheme.outline) },
        )
        CashFlowTextField(
            value = viewModel.categoryId,
            onValueChange = { viewModel.categoryId = it },
            label = { Text("Category") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { Text("e.g. Income", color = MaterialTheme.colorScheme.outline) },
        )
        CashFlowTextField(
            value = viewModel.accountId,
            onValueChange = { viewModel.accountId = it },
            label = { Text("Account") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { Text("e.g. Credit Card", color = MaterialTheme.colorScheme.outline) },
        )

        CashFlowTextField(
            value = viewModel.selectedDate?.format(
                DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.FULL
                )
            ) ?: "",
            onValueChange = { },
            label = { Text("Date") },
            placeholder = { Text("e.g. 07/09/2025", color = MaterialTheme.colorScheme.outline) },
            trailingIcon = {
                IconButton(onClick = { viewModel.showDateDialog = !viewModel.showDateDialog }) {
                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null)
                }
            },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedCashFlowButton(onClick = {
                onCancelClicked()
            }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            CashFlowButton(onClick = {
                val newOutcome = Outcome(
                    title = viewModel.title,
                    details = viewModel.details,
                    amount = viewModel.amount.toDouble(),
                    categoryId = viewModel.categoryId,
                    accountId = viewModel.accountId,
                    date = viewModel.selectedDate.toString(),
                )
                viewModel.addOutcome(newOutcome)
                viewModel.loadOutcomes() // update outcomes
            }) {
                Text(modifier = Modifier.padding(horizontal = 12.dp), text = "Add")
            }
        }

        if (viewModel.showDateDialog) {
            DatePickerDialog(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(16.dp),
                onDismissRequest = { viewModel.showDateDialog = false },
                confirmButton = {
                    TextButton(
                        modifier = Modifier.padding(16.dp), onClick = {
                            viewModel.showDateDialog = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.selectedDate =
                                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                            }
                        }) {
                        Text("Select", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                },
                dismissButton = {
                    TextButton(modifier = Modifier.padding(16.dp), onClick = {
                        viewModel.showDateDialog = false
                        datePickerState.selectedDateMillis =
                            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
                                .toEpochMilli() // today's date
                    }) {
                        Text(
                            "Today", style = MaterialTheme.typography.titleSmall, color = Color.Blue
                        )
                    }
                }) {
                DatePicker(
                    datePickerState, title = {
                        Text(
                            text = "Pick a date.",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 24.dp, start = 24.dp)
                        )
                    })
            }
        }

    }
}
