package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Inventory
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _inventory = MutableStateFlow<List<Inventory>>(emptyList())
    val inventory: StateFlow<List<Inventory>> = _inventory.asStateFlow()
    
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    val packages: StateFlow<List<Package>> = _packages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadInventory()
        loadPackages()
    }
    
    fun loadInventory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllInventory().collect { inventoryList ->
                    _inventory.value = inventoryList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المخزون: ${e.message}"
            } finally {
                _isLoading.value = false
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
    
    fun addInventory(
        packageId: String,
        quantity: Int
    ) {
        if (packageId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار الباقة"
            return
        }
        
        if (quantity <= 0) {
            _errorMessage.value = "يرجى إدخال كمية صحيحة"
            return
        }
        
        viewModelScope.launch {
            try {
                val newInventory = Inventory(
                    id = repository.generateId(),
                    packageId = packageId,
                    quantity = quantity,
                    createdAt = repository.getCurrentDate()
                )
                
                repository.insertInventory(newInventory)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة المخزون: ${e.message}"
            }
        }
    }
    
    fun updateInventory(
        id: String,
        packageId: String,
        quantity: Int
    ) {
        if (packageId.isEmpty()) {
            _errorMessage.value = "يرجى اختيار الباقة"
            return
        }
        
        if (quantity <= 0) {
            _errorMessage.value = "يرجى إدخال كمية صحيحة"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingInventory = repository.getInventoryById(id)
                if (existingInventory != null) {
                    val updatedInventory = existingInventory.copy(
                        packageId = packageId,
                        quantity = quantity
                    )
                    
                    repository.updateInventory(updatedInventory)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث المخزون: ${e.message}"
            }
        }
    }
    
    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            try {
                repository.deleteInventory(inventory)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف المخزون: ${e.message}"
            }
        }
    }
    
    fun getPackageById(packageId: String): Package? {
        return packages.value.find { it.id == packageId }
    }
    
    fun getTotalQuantityByPackage(packageId: String): Int {
        return inventory.value.filter { it.packageId == packageId }.sumOf { it.quantity }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}