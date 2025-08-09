package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Package
import com.ngs.cards775396439.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
            _isLoading.value = true
            try {
                repository.getAllPackages().collect { packages ->
                    _packages.value = packages
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addPackage(name: String, retailPrice: Double?, wholesalePrice: Double?, distributorPrice: Double?) {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val packageId = "pkg_${System.currentTimeMillis()}"
                
                val package_ = Package(
                    id = packageId,
                    name = name,
                    retailPrice = retailPrice,
                    wholesalePrice = wholesalePrice,
                    distributorPrice = distributorPrice,
                    createdAt = today
                )
                
                repository.insertPackage(package_)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updatePackage(package_: Package) {
        viewModelScope.launch {
            try {
                repository.updatePackage(package_)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deletePackage(package_: Package) {
        viewModelScope.launch {
            try {
                repository.deletePackage(package_)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}