package com.example.cashflow

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cashflow.data.repository.AccountRepository
import com.example.cashflow.data.repository.CategoryRepository
import com.example.cashflow.data.repository.IncomeRepository
import com.example.cashflow.data.repository.OutcomeRepository
import com.example.cashflow.presentation.navigation.NavGraph
import com.example.cashflow.presentation.navigation.Route
import com.example.cashflow.presentation.theme.CashFlowTheme
import com.example.cashflow.presentation.ui.home.components.MyAppBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition
            }
        }

        /** Sample data insertion */
        val database = (application as CashFlowApplication).database
        val accountRepository = AccountRepository(database.accountDao())
        val incomeRepository = IncomeRepository(database.incomeDao())
        val outcomeRepository = OutcomeRepository(database.outcomeDao())
        val categoryRepository = CategoryRepository(database.categoryDao())

        lifecycleScope.launch(Dispatchers.IO) {
            sampleAccounts.forEach { account ->
                accountRepository.addAccount(account)
            }
            sampleIncomes.forEach { income ->
                incomeRepository.addIncome(income)
            }
            sampleOutcomes.forEach { outcome ->
                outcomeRepository.addOutcome(outcome)
            }
            sampleCategories.forEach { category ->
                categoryRepository.addCategory(category)
            }
        } // end of insertion

        enableEdgeToEdge()
        setContent {
            CashFlowTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(bottomBar = {
                    if (currentRoute != Route.OnBoardingScreen.route) MyAppBottomBar(
                        navController
                    )
                }) { innerPaddings ->
                    val startDestination = viewModel.startDestination
                    NavGraph(
                        startDestination = startDestination,
                        navController = navController,
                        innerPaddings = innerPaddings
                    )
                }
            }
        }
    }

}
