package com.example.cashflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashflow.domain.model.Outcome
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OutcomeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addOutcome(outcome: Outcome): Long

    @Query("SELECT * FROM outcomes WHERE id = :id")
    abstract fun getAnOutcomeById(id: String): Flow<Outcome?>

    @Query("SELECT * FROM outcomes")
    abstract fun getAllOutcomes(): Flow<List<Outcome>>

    @Update
    abstract suspend fun updateOutcome(outcome: Outcome): Int

    @Query("DELETE FROM outcomes")
    abstract suspend fun deleteAllOutcomes(): Int

    @Query("DELETE FROM outcomes WHERE id = :id")
    abstract suspend fun deleteOutcomeById(id: String): Int

}
