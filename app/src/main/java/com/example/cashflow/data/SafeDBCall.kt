package com.example.cashflow.data

import android.util.Log
import com.example.cashflow.R
import com.example.cashflow.util.LocalizationHelper
import kotlinx.coroutines.CancellationException

suspend fun <T> safeDbCall(call: suspend () -> T): ResultState<T> {
    return try {
        val result = call()
        val isEmpty = when (result) { // Check if result is empty for common types
            null -> true
            is Collection<*> -> result.isEmpty()
            is Array<*> -> result.isEmpty()
            is String -> result.isEmpty()
            else -> false
        }

        if (isEmpty) ResultState.Error(NoDataException(), message = "No data available")
        else ResultState.Success(result) // return the non-empty result.

    } catch (e: CancellationException) { // Propagate coroutine cancellation properly
        throw e
    } catch (e: Exception) {
        Log.e("logs", "DB: Exception occurred during call", e)
        ResultState.Error(
            e, e.localizedMessage ?: LocalizationHelper.getString(R.string.no_data_available)
        )
    }
}

/** Exception indicating that no data was found. */
class NoDataException(message: String = "No data available") : Exception(message)
