package com.example.cashflow.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashflow.data.dao.IncomeDao
import com.example.cashflow.data.dao.OutcomeDao
import com.example.cashflow.domain.model.Income
import com.example.cashflow.domain.model.Outcome

@Database(entities = [Income::class, Outcome::class], version = 2, exportSchema = false)
abstract class CashFlowDatabase : RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun outcomeDao(): OutcomeDao

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
