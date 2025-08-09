package com.ngs.cards775396439.data.repository

import com.ngs.cards775396439.data.AppDatabase
import com.ngs.cards775396439.data.dao.*
import com.ngs.cards775396439.data.entity.*
import kotlinx.coroutines.flow.Flow

class NetworkCardsRepository(private val database: AppDatabase) {
    
    // Package operations
    fun getAllPackages(): Flow<List<Package>> = database.packageDao().getAllPackages()
    suspend fun getPackageById(id: Long): Package? = database.packageDao().getPackageById(id)
    suspend fun insertPackage(package_: Package): Long = database.packageDao().insertPackage(package_)
    suspend fun updatePackage(package_: Package) = database.packageDao().updatePackage(package_)
    suspend fun deletePackage(package_: Package) = database.packageDao().deletePackage(package_)
    fun searchPackages(query: String): Flow<List<Package>> = database.packageDao().searchPackages(query)
    
    // Inventory operations
    fun getAllInventory(): Flow<List<Inventory>> = database.inventoryDao().getAllInventory()
    suspend fun getInventoryByPackageId(packageId: Long): Inventory? = database.inventoryDao().getInventoryByPackageId(packageId)
    suspend fun insertInventory(inventory: Inventory): Long = database.inventoryDao().insertInventory(inventory)
    suspend fun updateInventory(inventory: Inventory) = database.inventoryDao().updateInventory(inventory)
    suspend fun deleteInventory(inventory: Inventory) = database.inventoryDao().deleteInventory(inventory)
    fun getTotalQuantity(): Flow<Int?> = database.inventoryDao().getTotalQuantity()
    
    // Store operations
    fun getAllStores(): Flow<List<Store>> = database.storeDao().getAllStores()
    suspend fun getStoreById(id: Long): Store? = database.storeDao().getStoreById(id)
    suspend fun insertStore(store: Store): Long = database.storeDao().insertStore(store)
    suspend fun updateStore(store: Store) = database.storeDao().updateStore(store)
    suspend fun deleteStore(store: Store) = database.storeDao().deleteStore(store)
    fun searchStores(query: String): Flow<List<Store>> = database.storeDao().searchStores(query)
    
    // Expense operations
    fun getAllExpenses(): Flow<List<Expense>> = database.expenseDao().getAllExpenses()
    suspend fun getExpenseById(id: Long): Expense? = database.expenseDao().getExpenseById(id)
    suspend fun insertExpense(expense: Expense): Long = database.expenseDao().insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = database.expenseDao().updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = database.expenseDao().deleteExpense(expense)
    fun getTotalExpenses(): Flow<Double?> = database.expenseDao().getTotalExpenses()
    
    // Sale operations
    fun getAllSales(): Flow<List<Sale>> = database.saleDao().getAllSales()
    suspend fun getSaleById(id: Long): Sale? = database.saleDao().getSaleById(id)
    suspend fun insertSale(sale: Sale): Long = database.saleDao().insertSale(sale)
    suspend fun updateSale(sale: Sale) = database.saleDao().updateSale(sale)
    suspend fun deleteSale(sale: Sale) = database.saleDao().deleteSale(sale)
    fun getTotalSales(): Flow<Double?> = database.saleDao().getTotalSales()
    
    // Payment operations
    fun getAllPayments(): Flow<List<Payment>> = database.paymentDao().getAllPayments()
    suspend fun getPaymentById(id: Long): Payment? = database.paymentDao().getPaymentById(id)
    suspend fun insertPayment(payment: Payment): Long = database.paymentDao().insertPayment(payment)
    suspend fun updatePayment(payment: Payment) = database.paymentDao().updatePayment(payment)
    suspend fun deletePayment(payment: Payment) = database.paymentDao().deletePayment(payment)
    fun getTotalPayments(): Flow<Double?> = database.paymentDao().getTotalPayments()
}