package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.AccountDao
import com.example.cashflow.domain.model.Account

class AccountRepository(private val accountDao: AccountDao) {
    suspend fun addAccount(account: Account) = accountDao.addAccount(account)
    fun getAccountId(title: String) = accountDao.getAccountId(title)

    fun getAccounts() = accountDao.getAllAccounts()

    fun getAnAccountById(id: String) = accountDao.getAnAccountById(id)

    suspend fun updateAccount(account: Account) = accountDao.updateAccount(account)

    suspend fun deleteAnAccount(id: String) = accountDao.deleteAccountById(id)

    suspend fun deleteAllAccounts() = accountDao.deleteAllAccounts()
}
