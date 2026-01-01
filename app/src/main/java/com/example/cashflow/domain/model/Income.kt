package com.example.cashflow.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val title: String,
    override val details: String,
    override val amount: Double,
    override val categoryId: Int,
    override val accountId: Int,
    override val date: String,
) : Transaction(
    id, title, details, amount, categoryId, accountId, date, type = TransactionType.INCOME
)
