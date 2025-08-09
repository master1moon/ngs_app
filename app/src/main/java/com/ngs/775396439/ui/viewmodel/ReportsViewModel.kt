package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.*
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Dashboard Stats
    private val _totalPackages = MutableStateFlow(0)
    val totalPackages: StateFlow<Int> = _totalPackages.asStateFlow()
    
    private val _totalStores = MutableStateFlow(0)
    val totalStores: StateFlow<Int> = _totalStores.asStateFlow()
    
    private val _totalCards = MutableStateFlow(0)
    val totalCards: StateFlow<Int> = _totalCards.asStateFlow()
    
    private val _totalSales = MutableStateFlow(0.0)
    val totalSales: StateFlow<Double> = _totalSales.asStateFlow()
    
    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()
    
    private val _netProfit = MutableStateFlow(0.0)
    val netProfit: StateFlow<Double> = _netProfit.asStateFlow()
    
    // Reports Data
    private val _salesReport = MutableStateFlow<List<Sale>>(emptyList())
    val salesReport: StateFlow<List<Sale>> = _salesReport.asStateFlow()
    
    private val _expensesReport = MutableStateFlow<List<Expense>>(emptyList())
    val expensesReport: StateFlow<List<Expense>> = _expensesReport.asStateFlow()
    
    private val _paymentsReport = MutableStateFlow<List<Payment>>(emptyList())
    val paymentsReport: StateFlow<List<Payment>> = _paymentsReport.asStateFlow()
    
    init {
        loadDashboardStats()
        loadReports()
    }
    
    fun loadDashboardStats() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load packages count
                repository.getAllPackages().collect { packages ->
                    _totalPackages.value = packages.size
                }
                
                // Load stores count
                repository.getAllStores().collect { stores ->
                    _totalStores.value = stores.size
                }
                
                // Load total cards
                repository.getAllInventory().collect { inventory ->
                    _totalCards.value = inventory.sumOf { it.quantity }
                }
                
                // Load total sales
                repository.getAllSales().collect { sales ->
                    _totalSales.value = sales.sumOf { it.total }
                }
                
                // Load total expenses
                repository.getAllExpenses().collect { expenses ->
                    _totalExpenses.value = expenses.sumOf { it.amount }
                }
                
                // Calculate net profit
                _netProfit.value = _totalSales.value - _totalExpenses.value
                
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الإحصائيات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadReports() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load sales report
                repository.getAllSales().collect { sales ->
                    _salesReport.value = sales
                }
                
                // Load expenses report
                repository.getAllExpenses().collect { expenses ->
                    _expensesReport.value = expenses
                }
                
                // Load payments report
                repository.getAllPayments().collect { payments ->
                    _paymentsReport.value = payments
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل التقارير: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getSalesByDateRange(fromDate: String, toDate: String): List<Sale> {
        return salesReport.value.filter { sale ->
            sale.date >= fromDate && sale.date <= toDate
        }
    }
    
    fun getExpensesByDateRange(fromDate: String, toDate: String): List<Expense> {
        return expensesReport.value.filter { expense ->
            expense.date >= fromDate && expense.date <= toDate
        }
    }
    
    fun getPaymentsByDateRange(fromDate: String, toDate: String): List<Payment> {
        return paymentsReport.value.filter { payment ->
            payment.date >= fromDate && payment.date <= toDate
        }
    }
    
    fun getStoreBalance(storeId: String): Double {
        val storeSales = salesReport.value.filter { it.storeId == storeId }
        val storePayments = paymentsReport.value.filter { it.storeId == storeId }
        
        val totalSales = storeSales.sumOf { it.total }
        val totalPayments = storePayments.sumOf { it.amount }
        
        return totalSales - totalPayments
    }
    
    fun getNetProfitByDateRange(fromDate: String, toDate: String): Double {
        val salesInRange = getSalesByDateRange(fromDate, toDate)
        val expensesInRange = getExpensesByDateRange(fromDate, toDate)
        
        val totalSales = salesInRange.sumOf { it.total }
        val totalExpenses = expensesInRange.sumOf { it.amount }
        
        return totalSales - totalExpenses
    }
    
    fun getTopSellingPackages(limit: Int = 5): List<Pair<String, Int>> {
        val salesByPackage = salesReport.value
            .groupBy { it.packageId }
            .mapValues { (_, sales) -> sales.sumOf { it.quantity } }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
        
        return salesByPackage.map { (packageId, quantity) ->
            Pair(packageId, quantity)
        }
    }
    
    fun getTopStores(limit: Int = 5): List<Pair<String, Double>> {
        val salesByStore = salesReport.value
            .groupBy { it.storeId }
            .mapValues { (_, sales) -> sales.sumOf { it.total } }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
        
        return salesByStore.map { (storeId, total) ->
            Pair(storeId, total)
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}