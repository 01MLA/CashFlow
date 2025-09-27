package com.example.cashflow.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes_table")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val details: String,
    val amount: Double,
    val categoryId: String,
    val accountId: String,
    val date: String
)
