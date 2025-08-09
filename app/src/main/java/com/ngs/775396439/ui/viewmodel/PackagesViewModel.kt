package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackagesViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    val packages: StateFlow<List<Package>> = _packages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadPackages()
    }
    
    fun loadPackages() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllPackages().collect { packagesList ->
                    _packages.value = packagesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الباقات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addPackage(
        name: String,
        retailPrice: Double?,
        wholesalePrice: Double?,
        distributorPrice: Double?
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "يرجى إدخال اسم الباقة"
            return
        }
        
        viewModelScope.launch {
            try {
                val newPackage = Package(
                    id = repository.generateId(),
                    name = name.trim(),
                    retailPrice = retailPrice,
                    wholesalePrice = wholesalePrice,
                    distributorPrice = distributorPrice,
                    createdAt = repository.getCurrentDate(),
                    image = ""
                )
                
                repository.insertPackage(newPackage)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة الباقة: ${e.message}"
            }
        }
    }
    
    fun updatePackage(
        id: String,
        name: String,
        retailPrice: Double?,
        wholesalePrice: Double?,
        distributorPrice: Double?
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "يرجى إدخال اسم الباقة"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingPackage = repository.getPackageById(id)
                if (existingPackage != null) {
                    val updatedPackage = existingPackage.copy(
                        name = name.trim(),
                        retailPrice = retailPrice,
                        wholesalePrice = wholesalePrice,
                        distributorPrice = distributorPrice
                    )
                    
                    repository.updatePackage(updatedPackage)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث الباقة: ${e.message}"
            }
        }
    }
    
    fun deletePackage(package_: Package) {
        viewModelScope.launch {
            try {
                repository.deletePackage(package_)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف الباقة: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}