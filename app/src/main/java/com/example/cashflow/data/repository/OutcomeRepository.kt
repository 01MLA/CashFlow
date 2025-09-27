package com.example.cashflow.data.repository

import com.example.cashflow.data.dao.OutcomeDao
import com.example.cashflow.domain.model.Outcome

class OutcomeRepository(private val outcomeDao: OutcomeDao) {

    suspend fun addOutcome(outcome: Outcome) = outcomeDao.addOutcome(outcome)

    fun getOutcomes() = outcomeDao.getAllOutcomes()

    suspend fun getAnOutcomeById(id: String) = outcomeDao.getAnOutcomeById(id)

    suspend fun updateOutcome(outcome: Outcome) = outcomeDao.updateOutcome(outcome)

    suspend fun deleteAnOutcome(id: String) = outcomeDao.deleteOutcomeById(id)

    suspend fun deleteAllOutcomes() = outcomeDao.deleteAllOutcomes()

}
