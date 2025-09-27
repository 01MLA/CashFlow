package com.example.cashflow.util

import android.content.Context
import androidx.annotation.StringRes

object LocalizationHelper {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getString(@StringRes resId: Int): String {
        return appContext.getString(resId)
    }
}
