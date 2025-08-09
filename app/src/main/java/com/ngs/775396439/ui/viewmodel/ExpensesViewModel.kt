package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Expense
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpensesViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
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
            try {
                _isLoading.value = true
                repository.getAllExpenses().collect { expensesList ->
                    _expenses.value = expensesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المصروفات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addExpense(
        type: String,
        amount: Double,
        notes: String,
        date: String,
        addLater: Boolean
    ) {
        if (type.isBlank()) {
            _errorMessage.value = "يرجى إدخال نوع المصروف"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        if (notes.isBlank()) {
            _errorMessage.value = "يرجى إدخال ملاحظات المصروف"
            return
        }
        
        viewModelScope.launch {
            try {
                val newExpense = Expense(
                    id = repository.generateId(),
                    type = type.trim(),
                    amount = amount,
                    notes = notes.trim(),
                    date = date,
                    addLater = addLater
                )
                
                repository.insertExpense(newExpense)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة المصروف: ${e.message}"
            }
        }
    }
    
    fun updateExpense(
        id: String,
        type: String,
        amount: Double,
        notes: String,
        date: String,
        addLater: Boolean
    ) {
        if (type.isBlank()) {
            _errorMessage.value = "يرجى إدخال نوع المصروف"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        if (notes.isBlank()) {
            _errorMessage.value = "يرجى إدخال ملاحظات المصروف"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingExpense = repository.getExpenseById(id)
                if (existingExpense != null) {
                    val updatedExpense = existingExpense.copy(
                        type = type.trim(),
                        amount = amount,
                        notes = notes.trim(),
                        date = date,
                        addLater = addLater
                    )
                    
                    repository.updateExpense(updatedExpense)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث المصروف: ${e.message}"
            }
        }
    }
    
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف المصروف: ${e.message}"
            }
        }
    }
    
    fun getExpenseById(id: String): Expense? {
        return expenses.value.find { it.id == id }
    }
    
    fun getTotalExpenses(): Double {
        return expenses.value.sumOf { it.amount }
    }
    
    fun getExpensesByDateRange(fromDate: String, toDate: String): List<Expense> {
        return expenses.value.filter { expense ->
            expense.date >= fromDate && expense.date <= toDate
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}