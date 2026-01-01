package com.example.cashflow.util

import java.text.NumberFormat
import java.util.Locale

fun formatMoney(amount: Double, currency: String): String {
    val formater = NumberFormat.getInstance(Locale.getDefault())
    return formater.format(amount) + " $currency"
}
