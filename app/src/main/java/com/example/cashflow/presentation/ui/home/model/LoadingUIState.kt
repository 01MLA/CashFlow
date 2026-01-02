package com.example.cashflow.presentation.ui.home.model

sealed class LoadingUIState<out T> {
    object Idle : LoadingUIState<Nothing>()
    object Loading : LoadingUIState<Nothing>()
    data class Success<T>(val data: T) : LoadingUIState<T>()
    data class Error(val exception: Throwable? = null, val message: String) :
        LoadingUIState<Nothing>()
}
