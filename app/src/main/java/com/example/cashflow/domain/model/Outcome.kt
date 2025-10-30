package com.example.cashflow.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outcomes")
data class Outcome(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val details: String,
    val amount: Double,
    val categoryId: Int,
    val accountId: Int,
    val date: String
)
