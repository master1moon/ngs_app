package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ExpensesViewModel : ViewModel() {
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadExpenses()
    }
    
    fun loadExpenses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Add some sample data for testing
                val sampleExpenses = listOf(
                    Expense(
                        id = "exp_1",
                        type = "كهرباء",
                        amount = 150.0,
                        notes = "فاتورة الكهرباء الشهرية",
                        date = "2024-08-01",
                        addLater = false
                    ),
                    Expense(
                        id = "exp_2",
                        type = "انترنت ADSL",
                        amount = 80.0,
                        notes = "اشتراك الإنترنت",
                        date = "2024-08-02",
                        addLater = false
                    ),
                    Expense(
                        id = "exp_3",
                        type = "صيانة",
                        amount = 200.0,
                        notes = "صيانة المعدات",
                        date = "2024-08-03",
                        addLater = true
                    )
                )
                _expenses.value = sampleExpenses
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addExpense(type: String, amount: Double, notes: String, date: String, addLater: Boolean) {
        viewModelScope.launch {
            try {
                val expenseId = "exp_${System.currentTimeMillis()}"
                
                val expense = Expense(
                    id = expenseId,
                    type = type,
                    amount = amount,
                    notes = notes,
                    date = date,
                    addLater = addLater
                )
                
                val currentList = _expenses.value.toMutableList()
                currentList.add(0, expense)
                _expenses.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val currentList = _expenses.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == expense.id }
                if (index != -1) {
                    currentList[index] = expense
                    _expenses.value = currentList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                val currentList = _expenses.value.toMutableList()
                currentList.removeAll { it.id == expense.id }
                _expenses.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun getTotalExpenses(): Double {
        return _expenses.value.sumOf { it.amount }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}