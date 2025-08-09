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

    private fun loadInventory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllInventory().collect { inventory ->
                    _inventory.value = inventory
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المخزون: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun loadPackages() {
        viewModelScope.launch {
            try {
                repository.getAllPackages().collect { packages ->
                    _packages.value = packages
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل الباقات: ${e.message}"
            }
        }
    }

    fun addInventory(
        packageId: String,
        quantity: Int,
        date: String = ""
    ) {
        viewModelScope.launch {
            try {
                if (packageId.isBlank()) {
                    _errorMessage.value = "يرجى اختيار الباقة"
                    return@launch
                }

                if (quantity <= 0) {
                    _errorMessage.value = "يرجى إدخال كمية صحيحة"
                    return@launch
                }

                val inventory = Inventory(
                    id = repository.generateId(),
                    packageId = packageId,
                    quantity = quantity,
                    createdAt = date.ifBlank { repository.getCurrentDate() }
                )

                repository.insertInventory(inventory)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة الكمية: ${e.message}"
            }
        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            try {
                if (inventory.packageId.isBlank()) {
                    _errorMessage.value = "يرجى اختيار الباقة"
                    return@launch
                }

                if (inventory.quantity <= 0) {
                    _errorMessage.value = "يرجى إدخال كمية صحيحة"
                    return@launch
                }

                repository.updateInventory(inventory)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث الكمية: ${e.message}"
            }
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            try {
                repository.deleteInventory(inventory)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف الكمية: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getPackageById(packageId: String): Package? {
        return _packages.value.find { it.id == packageId }
    }

    fun calculateTotalValue(inventory: Inventory, priceType: String): Double {
        val package_ = getPackageById(inventory.packageId) ?: return 0.0
        val price = when (priceType) {
            "retail" -> package_.retailPrice
            "wholesale" -> package_.wholesalePrice
            "distributor" -> package_.distributorPrice
            else -> package_.retailPrice
        }
        return (price ?: 0.0) * inventory.quantity
    }

    fun formatNumber(number: Double): String {
        return repository.formatNumber(number)
    }
}