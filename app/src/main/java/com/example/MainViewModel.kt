package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Expense
import com.example.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository
    val allExpenses: StateFlow<List<Expense>>

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _themeColorIndex = MutableStateFlow(0)
    val themeColorIndex: StateFlow<Int> = _themeColorIndex

    private val _monthlyLimit = MutableStateFlow(20000.0)
    val monthlyLimit: StateFlow<Double> = _monthlyLimit

    private val _isAppLockEnabled = MutableStateFlow(false)
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled

    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked

    private val _score = MutableStateFlow(100)
    val score: StateFlow<Int> = _score

    fun updateDashboardStats(expenses: List<Expense>) {
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentMonthSpent = expenses.filter { 
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = it.date
            it.category != "Lent" && it.category != "Received" && it.category != "Borrowed" &&
            cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
        
        val limit = _monthlyLimit.value
        val ratio = if (limit > 0) currentMonthSpent / limit else 0.0
        
        val calculatedScore = when {
            ratio <= 0.5 -> 90 + (Math.random() * 10).toInt()
            ratio <= 0.8 -> 70 + (Math.random() * 20).toInt()
            ratio <= 1.0 -> 50 + (Math.random() * 20).toInt()
            else -> 30 + (Math.random() * 20).toInt()
        }
        _score.value = calculatedScore
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putBoolean("isDarkMode", enabled).apply()
    }

    fun setThemeColorIndex(index: Int) {
        _themeColorIndex.value = index
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putInt("themeColorIndex", index).apply()
    }

    fun setMonthlyLimit(limit: Double) {
        _monthlyLimit.value = limit
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putFloat("monthlyLimit", limit.toFloat()).apply()
    }

    fun setAppLockEnabled(enabled: Boolean) {
        _isAppLockEnabled.value = enabled
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putBoolean("isAppLockEnabled", enabled).apply()
    }

    fun unlockApp() {
        _isAppUnlocked.value = true
    }

    fun login(name: String) {
        _userName.value = name
        _isLoggedIn.value = true
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("userName", name).putBoolean("isLoggedIn", true).apply()
    }

    fun logout() {
        _userName.value = null
        _isLoggedIn.value = false
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().remove("userName").putBoolean("isLoggedIn", false).apply()
    }

    init {
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean("isDarkMode", true)
        _themeColorIndex.value = prefs.getInt("themeColorIndex", 0)
        _monthlyLimit.value = prefs.getFloat("monthlyLimit", 20000.0f).toDouble()
        _userName.value = prefs.getString("userName", null)
        _isLoggedIn.value = prefs.getBoolean("isLoggedIn", false)
        val isLockEnabled = prefs.getBoolean("isAppLockEnabled", false)
        _isAppLockEnabled.value = isLockEnabled
        _isAppUnlocked.value = !isLockEnabled

        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
        allExpenses = repository.allExpenses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun deleteAllExpenses() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun addManualExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insert(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.update(expense)
        }
    }
}
