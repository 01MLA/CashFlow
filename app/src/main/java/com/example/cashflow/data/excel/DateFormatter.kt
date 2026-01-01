package com.example.cashflow.data.excel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    return date.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
    }
}
