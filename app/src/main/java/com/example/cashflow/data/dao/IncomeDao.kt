package com.example.cashflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashflow.domain.model.Income
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IncomeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addIncome(income: Income): Long

    @Query("SELECT * FROM incomes WHERE id = :id")
    abstract fun getAnIncomeById(id: String): Flow<Income?>

    @Query("SELECT * FROM incomes")
    abstract fun getAllIncomes(): Flow<List<Income>>

    @Update
    abstract suspend fun updateIncome(income: Income): Int

    @Query("DELETE FROM incomes")
    abstract suspend fun deleteAllIncomes(): Int

    @Query("DELETE FROM incomes WHERE id = :id")
    abstract suspend fun deleteIncomeById(id: String): Int

}
