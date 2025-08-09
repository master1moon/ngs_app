package com.ngs.cards775396439.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.cards775396439.data.entity.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StoresViewModel : ViewModel() {
    
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
            _isLoading.value = true
            try {
                // Simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Add some sample data for testing
                val sampleStores = listOf(
                    Store(
                        id = "store_1",
                        name = "محل الشرق",
                        priceType = "retail",
                        createdAt = "2024-08-01"
                    ),
                    Store(
                        id = "store_2", 
                        name = "محل الغرب",
                        priceType = "wholesale",
                        createdAt = "2024-08-02"
                    ),
                    Store(
                        id = "store_3",
                        name = "محل الوسط",
                        priceType = "distributor", 
                        createdAt = "2024-08-03"
                    )
                )
                _stores.value = sampleStores
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addStore(name: String, priceType: String) {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val storeId = "store_${System.currentTimeMillis()}"
                
                val store = Store(
                    id = storeId,
                    name = name,
                    priceType = priceType,
                    createdAt = today
                )
                
                val currentList = _stores.value.toMutableList()
                currentList.add(0, store)
                _stores.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateStore(store: Store) {
        viewModelScope.launch {
            try {
                val currentList = _stores.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == store.id }
                if (index != -1) {
                    currentList[index] = store
                    _stores.value = currentList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteStore(store: Store) {
        viewModelScope.launch {
            try {
                val currentList = _stores.value.toMutableList()
                currentList.removeAll { it.id == store.id }
                _stores.value = currentList
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun getPriceTypeName(priceType: String): String {
        return when (priceType) {
            "retail" -> "سعر التجزئة"
            "wholesale" -> "سعر الجملة"
            "distributor" -> "سعر الموزعين"
            else -> "غير محدد"
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}