package com.example.cashflow.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashflow.domain.model.Account
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addAccount(account: Account): Long

    @Query("SELECT id FROM accounts WHERE title = :title")
    abstract fun getAccountId(title: String): Int

    @Query("SELECT * FROM accounts WHERE id = :id")
    abstract fun getAnAccountById(id: String): Flow<Account?>

    @Query("SELECT * FROM accounts")
    abstract fun getAllAccounts(): Flow<List<Account>>

    @Update
    abstract suspend fun updateAccount(account: Account): Int

    @Query("DELETE FROM accounts")
    abstract suspend fun deleteAllAccounts(): Int

    @Query("DELETE FROM accounts WHERE id = :id")
    abstract suspend fun deleteAccountById(id: String): Int

}
