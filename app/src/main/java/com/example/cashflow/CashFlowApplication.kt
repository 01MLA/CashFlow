package com.example.cashflow

import android.app.Application
import com.example.cashflow.data.CashFlowDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CashFlowApplication : Application() {
    val database: CashFlowDatabase by lazy { CashFlowDatabase.getDatabase(this) }
}
