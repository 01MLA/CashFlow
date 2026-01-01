package com.example.cashflow.presentation.ui.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoneyOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cashflow.R
import com.example.cashflow.presentation.navigation.Route
import com.example.cashflow.presentation.ui.home.model.BottomItem

@SuppressLint("AutoboxingStateCreation")
@Composable
fun MyAppBottomBar(navController: NavController) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val bottomBarItems = listOf(
        BottomItem(
            0,
            stringResource(R.string.home),
            route = Route.HomeScreen.route,
            icon = if (selectedIndex == 0) Icons.Filled.Home else Icons.Outlined.Home
        ),
        BottomItem(
            1,
            stringResource(R.string.earnings),
            route = Route.EarningsScreen.route,
            icon = if (selectedIndex == 0) Icons.Filled.AttachMoney else Icons.Outlined.AttachMoney
        ),
        BottomItem(
            2,
            stringResource(R.string.expenses),
            route = Route.ExpensesScreen.route,
            icon = if (selectedIndex == 0) Icons.Filled.MoneyOff else Icons.Outlined.MoneyOff
        ),
    )

    NavigationBar(modifier = Modifier.height(85.dp)) {
        bottomBarItems.forEach { item ->
            NavigationBarItem(modifier = Modifier.padding(16.dp), label = {
                Text(
                    item.label,
                    fontWeight = if (item.index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selectedIndex == item.index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }, selected = item.index == selectedIndex, onClick = {
                selectedIndex = item.index
                navController.navigate(item.route)
            }, icon = {
                Icon(
                    item.icon,
                    null,
                    tint = if (selectedIndex == item.index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            })
        }
    }

}
