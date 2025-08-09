package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Sale
import com.ngs.cards775396439.data.entity.Store
import com.ngs.cards775396439.data.entity.Package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SalesViewModel : ViewModel() {
    
    private val _sales = MutableStateFlow<List<Sale>>(emptyList())
    val sales: StateFlow<List<Sale>> = _sales.asStateFlow()
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    val packages: StateFlow<List<Package>> = _packages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadSales()
        loadStores()
        loadPackages()
    }
    
    fun loadSales() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Add some sample data for testing
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
    
    fun loadPackages() {
        viewModelScope.launch {
            try {
                // Simulate loading packages
                kotlinx.coroutines.delay(500)
                _packages.value = emptyList() // Will be populated by PackagesViewModel
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun addSale(storeId: String, packageId: String, reason: String, quantity: Int, amount: Double, pricePerUnit: Double) {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val saleId = "sale_${System.currentTimeMillis()}"
                
                val sale = Sale(
                    id = saleId,
                    storeId = storeId,
                    packageId = packageId,
                    reason = reason,
                    quantity = quantity,
                    amount = amount,
                    pricePerUnit = pricePerUnit,
                    total = amount,
                    date = today
                )
                
                val currentList = _sales.value.toMutableList()
                currentList.add(0, sale)
                _sales.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateSale(sale: Sale) {
        viewModelScope.launch {
            try {
                val currentList = _sales.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == sale.id }
                if (index != -1) {
                    currentList[index] = sale
                    _sales.value = currentList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteSale(sale: Sale) {
        viewModelScope.launch {
            try {
                val currentList = _sales.value.toMutableList()
                currentList.removeAll { it.id == sale.id }
                _sales.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun getStoreById(storeId: String): Store? {
        return _stores.value.find { it.id == storeId }
    }
    
    fun getPackageById(packageId: String): Package? {
        return _packages.value.find { it.id == packageId }
    }
    
    fun getTotalSales(): Double {
        return _sales.value.sumOf { it.total }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}