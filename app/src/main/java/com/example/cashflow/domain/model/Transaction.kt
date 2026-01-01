package com.example.cashflow.domain.model

open class Transaction(
    open val id: Int = 0,
    open val title: String,
    open val details: String,
    open val amount: Double,
    open val categoryId: Int,
    open val accountId: Int,
    open val date: String,
    open var type: TransactionType,  // <-- add this
)

fun Transaction.toIncome(): Income = Income(id, title, details, amount, categoryId, accountId, date)
fun Transaction.toExpense(): Expense =
    Expense(id, title, details, amount, categoryId, accountId, date)

enum class TransactionType {
    INCOME, EXPENSE
}
