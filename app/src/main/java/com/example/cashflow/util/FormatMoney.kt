package com.example.cashflow.util

import java.text.NumberFormat
import java.util.Locale

fun formatMoney(amount: Double): String {
    val formater = NumberFormat.getInstance(Locale.getDefault())
    return formater.format(amount) + " AFG"
}
