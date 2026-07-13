package com.example.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesInRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesInRange(startDate, endDate)
    }

    suspend fun insert(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteAll() {
        expenseDao.deleteAllExpenses()
    }
}
