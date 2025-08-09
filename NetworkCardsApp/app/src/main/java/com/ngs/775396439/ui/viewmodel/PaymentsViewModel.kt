package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Payment
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentsViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadPayments()
        loadStores()
    }
    
    fun loadPayments() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllPayments().collect { paymentsList ->
                    _payments.value = paymentsList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المدفوعات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadStores() {
        viewModelScope.launch {
            try {
                repository.getAllStores().collect { storesList ->
                    _stores.value = storesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المحلات: ${e.message}"
            }
        }
    }
    
    fun addPayment(
        storeId: String,
        amount: Double,
        notes: String,
        date: String
    ) {
        if (storeId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار المحل"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        if (notes.isBlank()) {
            _errorMessage.value = "يرجى إدخال ملاحظات الدفع"
            return
        }
        
        viewModelScope.launch {
            try {
                val newPayment = Payment(
                    id = repository.generateId(),
                    storeId = storeId,
                    amount = amount,
                    notes = notes.trim(),
                    date = date
                )
                
                repository.insertPayment(newPayment)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة المدفوعات: ${e.message}"
            }
        }
    }
    
    fun updatePayment(
        id: String,
        storeId: String,
        amount: Double,
        notes: String,
        date: String
    ) {
        if (storeId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار المحل"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        if (notes.isBlank()) {
            _errorMessage.value = "يرجى إدخال ملاحظات الدفع"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingPayment = repository.getPaymentById(id)
                if (existingPayment != null) {
                    val updatedPayment = existingPayment.copy(
                        storeId = storeId,
                        amount = amount,
                        notes = notes.trim(),
                        date = date
                    )
                    
                    repository.updatePayment(updatedPayment)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث المدفوعات: ${e.message}"
            }
        }
    }
    
    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            try {
                repository.deletePayment(payment)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف المدفوعات: ${e.message}"
            }
        }
    }
    
    fun getPaymentById(id: String): Payment? {
        return payments.value.find { it.id == id }
    }
    
    fun getStoreById(id: String): Store? {
        return stores.value.find { it.id == id }
    }
    
    fun getTotalPayments(): Double {
        return payments.value.sumOf { it.amount }
    }
    
    fun getPaymentsByStore(storeId: String): List<Payment> {
        return payments.value.filter { it.storeId == storeId }
    }
    
    fun getPaymentsByDateRange(fromDate: String, toDate: String): List<Payment> {
        return payments.value.filter { payment ->
            payment.date >= fromDate && payment.date <= toDate
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}