package com.example.cashflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashflow.domain.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses WHERE id = :id")
    abstract fun getAnExpenseById(id: Int): Flow<Expense?>

    @Query("SELECT * FROM expenses")
    abstract fun getAllExpenses(): Flow<List<Expense>>

    @Update
    abstract suspend fun updateExpense(expense: Expense): Int

    @Query("DELETE FROM expenses")
    abstract suspend fun deleteAllExpenses(): Int

    @Query("DELETE FROM expenses WHERE id = :id")
    abstract suspend fun deleteExpenseById(id: Int): Int

}
