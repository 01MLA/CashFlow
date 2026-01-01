package com.example.cashflow.presentation.util

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import java.util.Date

fun Cell.getString(): String {
    return when (cellType) {
        CellType.STRING -> stringCellValue
        CellType.NUMERIC -> numericCellValue.toLong().toString()
        CellType.BOOLEAN -> booleanCellValue.toString()
        CellType.BLANK -> ""
        else -> ""
    }
}

fun Cell.getDouble(): Double {
    return when (cellType) {
        CellType.NUMERIC -> numericCellValue
        CellType.STRING -> stringCellValue.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }
}

fun Cell.getDate(): Date? {
    return when {
        cellType == CellType.NUMERIC && org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(
            this
        ) -> dateCellValue

        else -> null
    }
}
