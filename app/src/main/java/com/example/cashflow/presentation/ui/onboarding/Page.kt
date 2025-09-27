package com.example.cashflow.presentation.ui.onboarding

import androidx.annotation.DrawableRes
import com.example.cashflow.R

data class Page(val title: String, val description: String, @DrawableRes val image: Int)

val pages = listOf(
    Page(
        title = "Welcome",
        description = "Discover and track your cash flow easily with our app.",
        image = R.drawable.app_logo
    ), Page(
        title = "Manage Expenses",
        description = "Add, edit, and monitor your daily expenses in one place.",
        image = R.drawable.app_logo
    ), Page(
        title = "Stay in Control",
        description = "Get insights into your spending habits and save more.",
        image = R.drawable.app_logo
    )
)
