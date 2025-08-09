package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsViewModel : ViewModel() {
    
    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()
    
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    val packages: StateFlow<List<Package>> = _packages.asStateFlow()
    
    private val _inventory = MutableStateFlow<List<Inventory>>(emptyList())
    val inventory: StateFlow<List<Inventory>> = _inventory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Add sample data for reports
                val sampleSales = listOf(
                    Sale(
                        id = "sale_1",
                        storeId = "store_1",
                        packageId = "pkg_1",
                        reason = "بيع عادي",
                        quantity = 10,
                        amount = 100.0,
                        pricePerUnit = 10.0,
                        total = 100.0,
                        date = "2024-08-01"
                    ),
                    Sale(
                        id = "sale_2",
                        storeId = "store_2",
                        packageId = "pkg_2",
                        reason = "بيع جملة",
                        quantity = 50,
                        amount = 400.0,
                        pricePerUnit = 8.0,
                        total = 400.0,
                        date = "2024-08-02"
                    )
                )
                _sales.value = sampleSales
                
                val samplePayments = listOf(
                    Payment(
                        id = "pay_1",
                        storeId = "store_1",
                        amount = 500.0,
                        notes = "تسديد دفعة شهرية",
                        date = "2024-08-01"
                    )
                )
                _payments.value = samplePayments
                
                val sampleExpenses = listOf(
                    Expense(
                        id = "exp_1",
                        type = "كهرباء",
                        amount = 150.0,
                        notes = "فاتورة الكهرباء الشهرية",
                        date = "2024-08-01",
                        addLater = false
                    )
                )
                _expenses.value = sampleExpenses
                
                _stores.value = emptyList()
                _packages.value = emptyList()
                _inventory.value = emptyList()
                
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getTotalSales(): Double {
        return _sales.value.sumOf { it.total }
    }
    
    fun getTotalPayments(): Double {
        return _payments.value.sumOf { it.amount }
    }
    
    fun getTotalExpenses(): Double {
        return _expenses.value.sumOf { it.amount }
    }
    
    fun getNetProfit(): Double {
        return getTotalSales() + getTotalPayments() - getTotalExpenses()
    }
    
    fun getSalesByDateRange(fromDate: String, toDate: String): List<Sale> {
        return _sales.value.filter { sale ->
            sale.date >= fromDate && sale.date <= toDate
        }
    }
    
    fun getPaymentsByDateRange(fromDate: String, toDate: String): List<Payment> {
        return _payments.value.filter { payment ->
            payment.date >= fromDate && payment.date <= toDate
        }
    }
    
    fun getExpensesByDateRange(fromDate: String, toDate: String): List<Expense> {
        return _expenses.value.filter { expense ->
            expense.date >= fromDate && expense.date <= toDate
        }
    }
    
    fun getSalesByStore(storeId: String): List<Sale> {
        return _sales.value.filter { it.storeId == storeId }
    }
    
    fun getPaymentsByStore(storeId: String): List<Payment> {
        return _payments.value.filter { it.storeId == storeId }
    }
    
    fun getStoreBalance(storeId: String): Double {
        val storeSales = getSalesByStore(storeId).sumOf { it.total }
        val storePayments = getPaymentsByStore(storeId).sumOf { it.amount }
        return storeSales - storePayments
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}