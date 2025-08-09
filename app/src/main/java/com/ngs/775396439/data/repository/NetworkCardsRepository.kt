package com.ngs.`775396439`.data.repository

import com.ngs.`775396439`.data.AppDatabase
import com.ngs.`775396439`.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class NetworkCardsRepository(private val database: AppDatabase) {
    
    // Package Operations
    fun getAllPackages(): Flow<List<Package>> = database.packageDao().getAllPackages()
    suspend fun getPackageById(id: String): Package? = database.packageDao().getPackageById(id)
    suspend fun insertPackage(package_: Package) = database.packageDao().insertPackage(package_)
    suspend fun updatePackage(package_: Package) = database.packageDao().updatePackage(package_)
    suspend fun deletePackage(package_: Package) = database.packageDao().deletePackage(package_)
    suspend fun getRecentPackages(limit: Int): List<Package> = database.packageDao().getRecentPackages(limit)
    suspend fun getPackagesCount(): Int = database.packageDao().getPackagesCount()
    
    // Inventory Operations
    fun getAllInventory(): Flow<List<Inventory>> = database.inventoryDao().getAllInventory()
    suspend fun getInventoryById(id: String): Inventory? = database.inventoryDao().getInventoryById(id)
    suspend fun insertInventory(inventory: Inventory) = database.inventoryDao().insertInventory(inventory)
    suspend fun updateInventory(inventory: Inventory) = database.inventoryDao().updateInventory(inventory)
    suspend fun deleteInventory(inventory: Inventory) = database.inventoryDao().deleteInventory(inventory)
    suspend fun getInventoryByPackage(packageId: String): List<Inventory> = database.inventoryDao().getInventoryByPackage(packageId)
    suspend fun getTotalCardsByPackage(packageId: String): Int = database.inventoryDao().getTotalCardsByPackage(packageId) ?: 0
    suspend fun getTotalCards(): Int = database.inventoryDao().getTotalCards() ?: 0
    suspend fun getInventoryCount(): Int = database.inventoryDao().getInventoryCount()
    
    // Store Operations
    fun getAllStores(): Flow<List<Store>> = database.storeDao().getAllStores()
    suspend fun getStoreById(id: String): Store? = database.storeDao().getStoreById(id)
    suspend fun insertStore(store: Store) = database.storeDao().insertStore(store)
    suspend fun updateStore(store: Store) = database.storeDao().updateStore(store)
    suspend fun deleteStore(store: Store) = database.storeDao().deleteStore(store)
    suspend fun getRecentStores(limit: Int): List<Store> = database.storeDao().getRecentStores(limit)
    suspend fun getStoresCount(): Int = database.storeDao().getStoresCount()
    
    // Expense Operations
    fun getAllExpenses(): Flow<List<Expense>> = database.expenseDao().getAllExpenses()
    suspend fun getExpenseById(id: String): Expense? = database.expenseDao().getExpenseById(id)
    suspend fun insertExpense(expense: Expense) = database.expenseDao().insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = database.expenseDao().updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = database.expenseDao().deleteExpense(expense)
    suspend fun getTotalExpenses(): Double = database.expenseDao().getTotalExpenses() ?: 0.0
    suspend fun getTotalExpensesByDateRange(fromDate: String, toDate: String): Double = database.expenseDao().getTotalExpensesByDateRange(fromDate, toDate) ?: 0.0
    suspend fun getExpensesCount(): Int = database.expenseDao().getExpensesCount()
    
    // Sale Operations
    fun getAllSales(): Flow<List<Sale>> = database.saleDao().getAllSales()
    suspend fun getSaleById(id: String): Sale? = database.saleDao().getSaleById(id)
    suspend fun insertSale(sale: Sale) = database.saleDao().insertSale(sale)
    suspend fun updateSale(sale: Sale) = database.saleDao().updateSale(sale)
    suspend fun deleteSale(sale: Sale) = database.saleDao().deleteSale(sale)
    suspend fun getSalesByStore(storeId: String): List<Sale> = database.saleDao().getSalesByStore(storeId)
    suspend fun getTotalSalesByStore(storeId: String): Double = database.saleDao().getTotalSalesByStore(storeId) ?: 0.0
    suspend fun getTotalSales(): Double = database.saleDao().getTotalSales() ?: 0.0
    suspend fun getTotalSalesByDateRange(fromDate: String, toDate: String): Double = database.saleDao().getTotalSalesByDateRange(fromDate, toDate) ?: 0.0
    suspend fun getSalesCount(): Int = database.saleDao().getSalesCount()
    
    // Payment Operations
    fun getAllPayments(): Flow<List<Payment>> = database.paymentDao().getAllPayments()
    suspend fun getPaymentById(id: String): Payment? = database.paymentDao().getPaymentById(id)
    suspend fun insertPayment(payment: Payment) = database.paymentDao().insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = database.paymentDao().updatePayment(payment)
    suspend fun deletePayment(payment: Payment) = database.paymentDao().deletePayment(payment)
    suspend fun getPaymentsByStore(storeId: String): List<Payment> = database.paymentDao().getPaymentsByStore(storeId)
    suspend fun getTotalPaymentsByStore(storeId: String): Double = database.paymentDao().getTotalPaymentsByStore(storeId) ?: 0.0
    suspend fun getTotalPayments(): Double = database.paymentDao().getTotalPayments() ?: 0.0
    suspend fun getTotalPaymentsByDateRange(fromDate: String, toDate: String): Double = database.paymentDao().getTotalPaymentsByDateRange(fromDate, toDate) ?: 0.0
    suspend fun getPaymentsCount(): Int = database.paymentDao().getPaymentsCount()
    
    // Utility Functions
    fun generateId(): String = "id_${System.currentTimeMillis()}"
    
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun formatNumber(number: Double): String {
        return String.format("%,.0f", number)
    }
    
    fun parseFormattedNumber(text: String): Double {
        return try {
            text.replace(",", "").toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
    
    // Business Logic Functions
    suspend fun getStoreBalance(storeId: String): Double {
        val totalSales = getTotalSalesByStore(storeId)
        val totalPayments = getTotalPaymentsByStore(storeId)
        return totalSales - totalPayments
    }
    
    suspend fun getNetProfit(): Double {
        val totalSales = getTotalSales()
        val totalExpenses = getTotalExpenses()
        return totalSales - totalExpenses
    }
    
    suspend fun getNetProfitByDateRange(fromDate: String, toDate: String): Double {
        val totalSales = getTotalSalesByDateRange(fromDate, toDate)
        val totalExpenses = getTotalExpensesByDateRange(fromDate, toDate)
        return totalSales - totalExpenses
    }
}