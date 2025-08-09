package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Inventory
import com.ngs.cards775396439.data.entity.Package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InventoryViewModel : ViewModel() {
    
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
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                _inventory.value = emptyList()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadPackages() {
        viewModelScope.launch {
            try {
                // Simulate loading packages
                kotlinx.coroutines.delay(500)
                _packages.value = emptyList()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun addInventory(packageId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val inventoryId = "inv_${System.currentTimeMillis()}"
                
                val inventory = Inventory(
                    id = inventoryId,
                    packageId = packageId,
                    quantity = quantity,
                    createdAt = today
                )
                
                val currentList = _inventory.value.toMutableList()
                currentList.add(0, inventory)
                _inventory.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            try {
                val currentList = _inventory.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == inventory.id }
                if (index != -1) {
                    currentList[index] = inventory
                    _inventory.value = currentList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            try {
                val currentList = _inventory.value.toMutableList()
                currentList.removeAll { it.id == inventory.id }
                _inventory.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun getPackageById(packageId: String): Package? {
        return packages.value.find { it.id == packageId }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}