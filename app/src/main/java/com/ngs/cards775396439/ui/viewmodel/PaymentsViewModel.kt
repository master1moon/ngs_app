package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Payment
import com.ngs.cards775396439.data.entity.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PaymentsViewModel : ViewModel() {
    
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
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Add some sample data for testing
                val samplePayments = listOf(
                    Payment(
                        id = "pay_1",
                        storeId = "store_1",
                        amount = 500.0,
                        notes = "تسديد دفعة شهرية",
                        date = "2024-08-01"
                    ),
                    Payment(
                        id = "pay_2",
                        storeId = "store_2",
                        amount = 300.0,
                        notes = "تسديد جزئي",
                        date = "2024-08-02"
                    )
                )
                _payments.value = samplePayments
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadStores() {
        viewModelScope.launch {
            try {
                // Simulate loading stores
                kotlinx.coroutines.delay(500)
                _stores.value = emptyList() // Will be populated by StoresViewModel
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun addPayment(storeId: String, amount: Double, notes: String, date: String) {
        viewModelScope.launch {
            try {
                val paymentId = "pay_${System.currentTimeMillis()}"
                
                val payment = Payment(
                    id = paymentId,
                    storeId = storeId,
                    amount = amount,
                    notes = notes,
                    date = date
                )
                
                val currentList = _payments.value.toMutableList()
                currentList.add(0, payment)
                _payments.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updatePayment(payment: Payment) {
        viewModelScope.launch {
            try {
                val currentList = _payments.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == payment.id }
                if (index != -1) {
                    currentList[index] = payment
                    _payments.value = currentList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            try {
                val currentList = _payments.value.toMutableList()
                currentList.removeAll { it.id == payment.id }
                _payments.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun getStoreById(storeId: String): Store? {
        return _stores.value.find { it.id == storeId }
    }
    
    fun getTotalPayments(): Double {
        return _payments.value.sumOf { it.amount }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}