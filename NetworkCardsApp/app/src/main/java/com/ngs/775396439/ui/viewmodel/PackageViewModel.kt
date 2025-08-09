package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Package
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PackageViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _packages = MutableStateFlow<List<Package>>(emptyList())
    val packages: StateFlow<List<Package>> = _packages
    
    init {
        loadPackages()
    }
    
    private fun loadPackages() {
        viewModelScope.launch {
            repository.getAllPackages().collect { packages ->
                _packages.value = packages
            }
        }
    }
    
    fun addPackage(
        name: String,
        retailPrice: Double?,
        wholesalePrice: Double?,
        distributorPrice: Double?
    ) {
        viewModelScope.launch {
            val package_ = Package(
                id = repository.generateId(),
                name = name,
                retailPrice = retailPrice,
                wholesalePrice = wholesalePrice,
                distributorPrice = distributorPrice,
                createdAt = repository.getCurrentDate()
            )
            repository.insertPackage(package_)
        }
    }
    
    fun updatePackage(package_: Package) {
        viewModelScope.launch {
            repository.updatePackage(package_)
        }
    }
    
    fun deletePackage(package_: Package) {
        viewModelScope.launch {
            repository.deletePackage(package_)
        }
    }
}