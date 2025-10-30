package com.example.cashflow.presentation.ui.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashflow.R
import com.example.cashflow.domain.model.Income
import com.example.cashflow.presentation.components.CashFlowButton
import com.example.cashflow.presentation.components.CashFlowSelector
import com.example.cashflow.presentation.components.CashFlowTextField
import com.example.cashflow.presentation.components.OutlinedCashFlowButton
import com.example.cashflow.presentation.shimmerEffect
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewEarningScreen(onCancelClicked: () -> Unit = {}, viewModel: EarningsViewModel) {
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
        Image(
            painter = painterResource(R.drawable.receipt),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            "Add new income.",
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
        CashFlowTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Title") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            placeholder = { Text("e.g. Salary", color = MaterialTheme.colorScheme.outline) },
            isError = viewModel.titleError != null,
            singleLine = true
        )
        CashFlowTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            placeholder = { Text("e.g. 12000", color = MaterialTheme.colorScheme.outline) },
            isError = viewModel.amountError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            )
        )
        CashFlowTextField(
            value = viewModel.details,
            onValueChange = { viewModel.details = it },
            label = { Text("Details") },
            placeholder = {
                Text("e.g. January salary", color = MaterialTheme.colorScheme.outline)
            },
            isError = viewModel.detailsError != null,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            )
        )
        CashFlowSelector(
            modifier = Modifier.fillMaxWidth(),
            items = viewModel.categories.value.map { it.title },
            selectedItem = viewModel.selectedCategory,
            onItemSelected = { viewModel.selectedCategory = it },
            isError = viewModel.categoryError != null
        )
        CashFlowSelector(
            modifier = Modifier.fillMaxWidth(),
            items = viewModel.accounts.value.map { it.title },
            selectedItem = viewModel.selectedAccount,
            onItemSelected = { viewModel.selectedAccount = it },
            isError = viewModel.categoryError != null
        )
        CashFlowTextField(
            value = viewModel.selectedDate?.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        ) ?: "",
            onValueChange = { },
            label = { Text("Date") },
            placeholder = { Text("e.g. 07/09/2025", color = MaterialTheme.colorScheme.outline) },
            trailingIcon = {
                IconButton(onClick = { viewModel.showDateDialog = !viewModel.showDateDialog }) {
                    Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null)
                }
            },
            isError = viewModel.dateError != null,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = !viewModel.formErrorMessage.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = viewModel.formErrorMessage ?: "",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedCashFlowButton(onClick = {
                onCancelClicked()
                viewModel.resetForm()
            }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(16.dp))
            CashFlowButton(onClick = {
                if (viewModel.validateForm()) {
                    val newIncome = Income(
                        title = viewModel.title,
                        details = viewModel.details,
                        amount = viewModel.amount.toDoubleOrNull() ?: 0.0,
                        categoryId = viewModel.categories.value.find {
                            it.title == viewModel.selectedCategory
                        }?.id ?: 0,
                        accountId = viewModel.accounts.value.find {
                            it.title == viewModel.selectedAccount
                        }?.id ?: 0,
                        date = viewModel.selectedDate.toString(),
                    )
                    viewModel.addIncome(newIncome)
                    viewModel.resetForm()
                    viewModel.loadIncomes() // update incomes
                }
            }) {
                Text(modifier = Modifier.padding(horizontal = 12.dp), text = "Add")
            }
        }

        if (viewModel.showDateDialog) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
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
                                text = "Today",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Blue
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

}

@Composable
fun NewEarningScreenShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Image placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .shimmerEffect(true)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        // Title text placeholder
        Box(
            modifier = Modifier
                .height(24.dp)
                .fillMaxWidth(0.6f)
                .shimmerEffect(true)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                )
        )

        // TextField placeholders (Title, Amount, Details)
        repeat(3) {
            Box(
                modifier = Modifier
                    .height(if (it == 2) 60.dp else 48.dp)
                    .fillMaxWidth()
                    .shimmerEffect(true)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }

        // Selector placeholders (Category & Account)
        repeat(2) {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .shimmerEffect(true)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }

        // Date picker placeholder
        Box(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .shimmerEffect(true)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        // Buttons placeholder
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
                    .shimmerEffect(true)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
                    .shimmerEffect(true)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}
