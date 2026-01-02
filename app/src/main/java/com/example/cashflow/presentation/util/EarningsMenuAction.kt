package com.example.cashflow.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Input
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Output
import androidx.compose.ui.graphics.vector.ImageVector

sealed class EarningsMenuAction(val title: String, val icon: ImageVector) {
    object Import : EarningsMenuAction("Import Earnings", Icons.AutoMirrored.Outlined.Input)
    object Export : EarningsMenuAction("Export Earnings", Icons.Outlined.Output)
    object Delete : EarningsMenuAction("Delete all Earnings", Icons.Outlined.Delete)
}

sealed class ExpensesMenuAction(val title: String, val icon: ImageVector) {
    object Import : ExpensesMenuAction("Import Expenses", Icons.AutoMirrored.Outlined.Input)
    object Export : ExpensesMenuAction("Export Expenses", Icons.Outlined.Output)
    object Delete : ExpensesMenuAction("Delete all Expenses", Icons.Outlined.Delete)
}
