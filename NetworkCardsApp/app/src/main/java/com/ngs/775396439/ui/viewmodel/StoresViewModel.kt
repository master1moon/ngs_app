package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.Store
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoresViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadStores()
    }
    
    fun loadStores() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllStores().collect { storesList ->
                    _stores.value = storesList
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحميل المحلات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addStore(
        name: String,
        priceType: String
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "يرجى إدخال اسم المحل"
            return
        }
        
        if (priceType.isEmpty()) {
            _errorMessage.value = "يرجى اختيار نوع السعر"
            return
        }
        
        viewModelScope.launch {
            try {
                val newStore = Store(
                    id = repository.generateId(),
                    name = name.trim(),
                    priceType = priceType,
                    createdAt = repository.getCurrentDate()
                )
                
                repository.insertStore(newStore)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في إضافة المحل: ${e.message}"
            }
        }
    }
    
    fun updateStore(
        id: String,
        name: String,
        priceType: String
    ) {
        if (name.isBlank()) {
            _errorMessage.value = "يرجى إدخال اسم المحل"
            return
        }
        
        if (priceType.isEmpty()) {
            _errorMessage.value = "يرجى اختيار نوع السعر"
            return
        }
        
        viewModelScope.launch {
            try {
                val existingStore = repository.getStoreById(id)
                if (existingStore != null) {
                    val updatedStore = existingStore.copy(
                        name = name.trim(),
                        priceType = priceType
                    )
                    
                    repository.updateStore(updatedStore)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في تحديث المحل: ${e.message}"
            }
        }
    }
    
    fun deleteStore(store: Store) {
        viewModelScope.launch {
            try {
                repository.deleteStore(store)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في حذف المحل: ${e.message}"
            }
        }
    }
    
    fun getStoreById(id: String): Store? {
        return stores.value.find { it.id == id }
    }
    
    fun getPriceTypeDisplayName(priceType: String): String {
        return when (priceType) {
            "retail" -> "تجزئة"
            "wholesale" -> "جملة"
            "distributor" -> "موزعين"
            else -> "غير محدد"
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}