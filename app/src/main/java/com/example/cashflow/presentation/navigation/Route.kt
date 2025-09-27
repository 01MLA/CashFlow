package com.example.cashflow.presentation.navigation

sealed class Route(val route: String) {

    object OnBoardingScreen : Route("onBoarding_screen")
    object HomeScreen : Route("home_screen")
    object SearchScreen : Route("search_screen")
    object DetailsScreen : Route("details_screen")
    object EarningsScreen : Route("earnings_screen")
    object NewEarningScreen : Route("new_earning_screen")
    object ExpensesScreen : Route("expenses_screen")
    object NewExpenseScreen : Route("new_expense_screen")

    object BookMarkScreen : Route("bookmark_screen")
    object CashFlowNavigationScreen : Route("cashFlow_nav_screen")

    // Navigation sub-graphs
    object AppStartNavigation : Route("appStartNavigation")
    object CashFlowNavigation : Route("cashFlowNavigation")
}
