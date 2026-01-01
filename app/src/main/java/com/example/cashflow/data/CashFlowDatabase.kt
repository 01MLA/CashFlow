package com.example.cashflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashflow.data.dao.AccountDao
import com.example.cashflow.data.dao.CategoryDao
import com.example.cashflow.data.dao.ExpenseDao
import com.example.cashflow.data.dao.IncomeDao
import com.example.cashflow.domain.model.Account
import com.example.cashflow.domain.model.Category
import com.example.cashflow.domain.model.Expense
import com.example.cashflow.domain.model.Income

@Database(
    entities = [Income::class, Expense::class, Account::class, Category::class],
    version = 7,
    exportSchema = false
)
abstract class CashFlowDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: CashFlowDatabase? = null

        fun getDatabase(context: Context): CashFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, CashFlowDatabase::class.java, "cashflow_db"
                ).fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
