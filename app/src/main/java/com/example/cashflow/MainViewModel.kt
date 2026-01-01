package com.example.cashflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashflow.domain.useCases.AppEntryUseCases
import com.example.cashflow.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(appEntryUseCases: AppEntryUseCases) : ViewModel() {
    var splashCondition by mutableStateOf(true)
        private set
    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
        private set

    init {
        appEntryUseCases.readAppEntry().onEach { startFromHome ->
            startDestination =
                if (startFromHome) Route.CashFlowNavigation.route else Route.AppStartNavigation.route
            splashCondition = false
        }.launchIn(viewModelScope)
    }
}
