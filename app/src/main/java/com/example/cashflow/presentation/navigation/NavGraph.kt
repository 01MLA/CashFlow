package com.example.cashflow.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.cashflow.presentation.ui.home.HomeScreen
import com.example.cashflow.presentation.ui.onboarding.OnBoardingScreen
import com.example.cashflow.presentation.ui.onboarding.OnBoardingViewModel
import com.example.cashflow.presentation.ui.transactions.EarningsScreen
import com.example.cashflow.presentation.ui.transactions.EarningsViewModel
import com.example.cashflow.presentation.ui.transactions.ExpensesScreen
import com.example.cashflow.presentation.ui.transactions.ExpensesViewModel
import com.example.cashflow.presentation.ui.transactions.NewEarningScreen
import com.example.cashflow.presentation.ui.transactions.NewExpenseScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    innerPaddings: PaddingValues
) {
    NavHost(navController = navController, startDestination = startDestination) {
        navigation(
            route = Route.AppStartNavigation.route, startDestination = Route.OnBoardingScreen.route
        ) {
            composable(route = Route.OnBoardingScreen.route) {
                val viewModel: OnBoardingViewModel = hiltViewModel()
                OnBoardingScreen(event = viewModel::onEvent)
            }
        }

        // Navigation of rest of the app
        navigation(
            route = Route.CashFlowNavigation.route, startDestination = Route.HomeScreen.route
        ) {
            composable(route = Route.HomeScreen.route) {
                HomeScreen(navController)
            }
            composable(route = Route.NewEarningScreen.route) {
                val viewModel: EarningsViewModel = viewModel()
                NewEarningScreen({}, viewModel)
            }
            composable(route = Route.NewExpenseScreen.route) {
                val viewModel: ExpensesViewModel = viewModel()
                NewExpenseScreen({}, viewModel)
            }
            composable(route = Route.EarningsScreen.route) {
                val viewModel: EarningsViewModel = viewModel()
                EarningsScreen(innerPaddings, viewModel)
            }
            composable(route = Route.ExpensesScreen.route) {
                val viewModel: ExpensesViewModel = viewModel()
                ExpensesScreen(innerPaddings, viewModel)
            }
        }

    }
}
