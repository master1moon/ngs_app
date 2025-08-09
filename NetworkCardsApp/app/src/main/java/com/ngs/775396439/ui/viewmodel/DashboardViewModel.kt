package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _dashboardData = MutableStateFlow(DashboardData())
    val dashboardData: StateFlow<DashboardData> = _dashboardData
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                repository.getAllPackages(),
                repository.getAllInventory(),
                repository.getAllStores(),
                repository.getAllSales(),
                repository.getAllExpenses()
            ) { packages, inventory, stores, sales, expenses ->
                DashboardData(
                    packagesCount = packages.size,
                    totalCards = inventory.sumOf { it.quantity },
                    storesCount = stores.size,
                    totalSales = sales.sumOf { it.total },
                    totalExpenses = expenses.sumOf { it.amount },
                    netProfit = sales.sumOf { it.total } - expenses.sumOf { it.amount },
                    recentPackages = packages.take(3),
                    recentStores = stores.take(3)
                )
            }.collect { data ->
                _dashboardData.value = data
            }
        }
    }
    
    data class DashboardData(
        val packagesCount: Int = 0,
        val totalCards: Int = 0,
        val storesCount: Int = 0,
        val totalSales: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val netProfit: Double = 0.0,
        val recentPackages: List<com.ngs.`775396439`.data.entity.Package> = emptyList(),
        val recentStores: List<com.ngs.`775396439`.data.entity.Store> = emptyList()
    )
}