package com.example.cashflow

import com.example.cashflow.domain.model.Account
import com.example.cashflow.domain.model.Category
import com.example.cashflow.domain.model.Income
import com.example.cashflow.domain.model.Outcome

val sampleAccounts = listOf(
    Account(0, "Salary", 10000.0),
    Account(1, "Programming", 40000.0),
    Account(2, "YouTube", 100000.0)
)
val sampleIncomes = listOf(
    Income(
        id = 1,
        title = "Salary",
        amount = 1200.0,
        details = "Monthly salary for October",
        categoryId = 1,
        accountId = 2,
        date = "2025-10-25"
    ), Income(
        id = 2,
        title = "Freelance Project",
        amount = 500.0,
        details = "UI design project for a client",
        categoryId = 2,
        accountId = 1,
        date = "2025-10-20"
    ), Income(
        id = 3,
        title = "Investment Return",
        amount = 250.0,
        details = "Quarterly dividend payment",
        categoryId = 3,
        accountId = 3,
        date = "2025-10-10"
    ), Income(
        id = 4,
        title = "Gift",
        amount = 100.0,
        details = "Birthday gift from a friend",
        categoryId = 4,
        accountId = 1,
        date = "2025-10-05"
    ), Income(
        id = 5,
        title = "App Sale",
        amount = 75.5,
        details = "Google Play app revenue",
        categoryId = 5,
        accountId = 2,
        date = "2025-09-30"
    )
)
val sampleOutcomes = listOf(
    Outcome(
        id = 1,
        title = "Groceries",
        amount = 200.0,
        details = "Weekly grocery shopping",
        categoryId = 1,
        accountId = 2,
        date = "2025-10-24"
    ), Outcome(
        id = 2,
        title = "Electricity Bill",
        amount = 80.0,
        details = "October electricity bill",
        categoryId = 2,
        accountId = 1,
        date = "2025-10-22"
    ), Outcome(
        id = 3,
        title = "Internet Subscription",
        amount = 50.0,
        details = "Monthly internet plan",
        categoryId = 3,
        accountId = 3,
        date = "2025-10-15"
    ), Outcome(
        id = 4,
        title = "Fuel",
        amount = 60.0,
        details = "Fuel for car",
        categoryId = 4,
        accountId = 1,
        date = "2025-10-10"
    ), Outcome(
        id = 5,
        title = "Dining Out",
        amount = 45.5,
        details = "Dinner at a restaurant",
        categoryId = 5,
        accountId = 2,
        date = "2025-10-05"
    )
)
val sampleCategories = listOf(
    Category(id = 1, title = "Food & Beverages"),
    Category(id = 2, title = "Transportation"),
    Category(id = 3, title = "Utilities"),
    Category(id = 4, title = "Entertainment"),
    Category(id = 5, title = "Health & Fitness"),
    Category(id = 6, title = "Education"),
    Category(id = 7, title = "Shopping"),
    Category(id = 8, title = "Travel"),
    Category(id = 9, title = "Savings"),
    Category(id = 10, title = "Miscellaneous")
)
