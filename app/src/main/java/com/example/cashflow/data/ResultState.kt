package com.example.cashflow.data

sealed class ResultState<out T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val exception: Throwable, val message: String? = null) : ResultState<Nothing>()
}
