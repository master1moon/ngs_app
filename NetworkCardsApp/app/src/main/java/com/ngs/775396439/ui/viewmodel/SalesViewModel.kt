package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Sale
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
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
            try {
                _isLoading.value = true
                repository.getAllSales().collect { salesList ->
                    _sales.value = salesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المبيعات: ${e.message}"
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
    
    fun loadPackages() {
        viewModelScope.launch {
            try {
                repository.getAllPackages().collect { packagesList ->
                    _packages.value = packagesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الباقات: ${e.message}"
            }
        }
    }
    
    fun addSale(
        storeId: String,
        packageId: String,
        reason: String,
        quantity: Int,
        amount: Double,
        pricePerUnit: Double,
        total: Double,
        date: String
    ) {
        if (storeId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار المحل"
            return
        }
        
        if (packageId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار الباقة"
            return
        }
        
        if (reason.isBlank()) {
            _errorMessage.value = "يرجى إدخال سبب البيع"
            return
        }
        
        if (quantity <= 0) {
            _errorMessage.value = "يرجى إدخال كمية صحيحة"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        viewModelScope.launch {
            try {
                val newSale = Sale(
                    id = repository.generateId(),
                    storeId = storeId,
                    packageId = packageId,
                    reason = reason.trim(),
                    quantity = quantity,
                    amount = amount,
                    pricePerUnit = pricePerUnit,
                    total = total,
                    date = date
                )
                
                repository.insertSale(newSale)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة المبيعات: ${e.message}"
            }
        }
    }
    
    fun updateSale(
        id: String,
        storeId: String,
        packageId: String,
        reason: String,
        quantity: Int,
        amount: Double,
        pricePerUnit: Double,
        total: Double,
        date: String
    ) {
        if (storeId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار المحل"
            return
        }
        
        if (packageId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار الباقة"
            return
        }
        
        if (reason.isBlank()) {
            _errorMessage.value = "يرجى إدخال سبب البيع"
            return
        }
        
        if (quantity <= 0) {
            _errorMessage.value = "يرجى إدخال كمية صحيحة"
            return
        }
        
        if (amount <= 0) {
            _errorMessage.value = "يرجى إدخال مبلغ صحيح"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingSale = repository.getSaleById(id)
                if (existingSale != null) {
                    val updatedSale = existingSale.copy(
                        storeId = storeId,
                        packageId = packageId,
                        reason = reason.trim(),
                        quantity = quantity,
                        amount = amount,
                        pricePerUnit = pricePerUnit,
                        total = total,
                        date = date
                    )
                    
                    repository.updateSale(updatedSale)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث المبيعات: ${e.message}"
            }
        }
    }
    
    fun deleteSale(sale: Sale) {
        viewModelScope.launch {
            try {
                repository.deleteSale(sale)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف المبيعات: ${e.message}"
            }
        }
    }
    
    fun getSaleById(id: String): Sale? {
        return sales.value.find { it.id == id }
    }
    
    fun getStoreById(id: String): Store? {
        return stores.value.find { it.id == id }
    }
    
    fun getPackageById(id: String): Package? {
        return packages.value.find { it.id == id }
    }
    
    fun getTotalSales(): Double {
        return sales.value.sumOf { it.total }
    }
    
    fun getSalesByStore(storeId: String): List<Sale> {
        return sales.value.filter { it.storeId == storeId }
    }
    
    fun getSalesByDateRange(fromDate: String, toDate: String): List<Sale> {
        return sales.value.filter { sale ->
            sale.date >= fromDate && sale.date <= toDate
        }
    }
    
    fun calculateTotal(quantity: Int, pricePerUnit: Double): Double {
        return quantity * pricePerUnit
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}