package com.ngs.`775396439`.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngs.`775396439`.data.entity.*
import com.ngs.`775396439`.data.repository.NetworkCardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ImportExportViewModel(private val repository: NetworkCardsRepository) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    fun exportAllData(): String {
        return try {
            val jsonObject = JSONObject()
            
            // Export packages
            val packagesArray = JSONArray()
            repository.getAllPackages().collect { packages ->
                packages.forEach { package_ ->
                    val packageObject = JSONObject().apply {
                        put("id", package_.id)
                        put("name", package_.name)
                        put("retailPrice", package_.retailPrice)
                        put("wholesalePrice", package_.wholesalePrice)
                        put("distributorPrice", package_.distributorPrice)
                        put("createdAt", package_.createdAt)
                        put("image", package_.image)
                    }
                    packagesArray.put(packageObject)
                }
            }
            jsonObject.put("packages", packagesArray)
            
            // Export inventory
            val inventoryArray = JSONArray()
            repository.getAllInventory().collect { inventory ->
                inventory.forEach { item ->
                    val inventoryObject = JSONObject().apply {
                        put("id", item.id)
                        put("packageId", item.packageId)
                        put("quantity", item.quantity)
                        put("createdAt", item.createdAt)
                    }
                    inventoryArray.put(inventoryObject)
                }
            }
            jsonObject.put("inventory", inventoryArray)
            
            // Export stores
            val storesArray = JSONArray()
            repository.getAllStores().collect { stores ->
                stores.forEach { store ->
                    val storeObject = JSONObject().apply {
                        put("id", store.id)
                        put("name", store.name)
                        put("priceType", store.priceType)
                        put("createdAt", store.createdAt)
                    }
                    storesArray.put(storeObject)
                }
            }
            jsonObject.put("stores", storesArray)
            
            // Export expenses
            val expensesArray = JSONArray()
            repository.getAllExpenses().collect { expenses ->
                expenses.forEach { expense ->
                    val expenseObject = JSONObject().apply {
                        put("id", expense.id)
                        put("type", expense.type)
                        put("amount", expense.amount)
                        put("notes", expense.notes)
                        put("date", expense.date)
                        put("addLater", expense.addLater)
                    }
                    expensesArray.put(expenseObject)
                }
            }
            jsonObject.put("expenses", expensesArray)
            
            // Export sales
            val salesArray = JSONArray()
            repository.getAllSales().collect { sales ->
                sales.forEach { sale ->
                    val saleObject = JSONObject().apply {
                        put("id", sale.id)
                        put("storeId", sale.storeId)
                        put("packageId", sale.packageId)
                        put("reason", sale.reason)
                        put("quantity", sale.quantity)
                        put("amount", sale.amount)
                        put("pricePerUnit", sale.pricePerUnit)
                        put("total", sale.total)
                        put("date", sale.date)
                    }
                    salesArray.put(saleObject)
                }
            }
            jsonObject.put("sales", salesArray)
            
            // Export payments
            val paymentsArray = JSONArray()
            repository.getAllPayments().collect { payments ->
                payments.forEach { payment ->
                    val paymentObject = JSONObject().apply {
                        put("id", payment.id)
                        put("storeId", payment.storeId)
                        put("amount", payment.amount)
                        put("notes", payment.notes)
                        put("date", payment.date)
                    }
                    paymentsArray.put(paymentObject)
                }
            }
            jsonObject.put("payments", paymentsArray)
            
            jsonObject.put("exportDate", repository.getCurrentDate())
            jsonObject.put("version", "1.2")
            
            jsonObject.toString()
        } catch (e: Exception) {
            throw Exception("خطأ في تصدير البيانات: ${e.message}")
        }
    }
    
    fun importAllData(jsonData: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val jsonObject = JSONObject(jsonData)
                
                // Import packages
                if (jsonObject.has("packages")) {
                    val packagesArray = jsonObject.getJSONArray("packages")
                    for (i in 0 until packagesArray.length()) {
                        val packageObject = packagesArray.getJSONObject(i)
                        val package_ = Package(
                            id = packageObject.getString("id"),
                            name = packageObject.getString("name"),
                            retailPrice = packageObject.getDouble("retailPrice"),
                            wholesalePrice = packageObject.getDouble("wholesalePrice"),
                            distributorPrice = packageObject.getDouble("distributorPrice"),
                            createdAt = packageObject.getString("createdAt"),
                            image = packageObject.getString("image")
                        )
                        repository.insertPackage(package_)
                    }
                }
                
                // Import inventory
                if (jsonObject.has("inventory")) {
                    val inventoryArray = jsonObject.getJSONArray("inventory")
                    for (i in 0 until inventoryArray.length()) {
                        val inventoryObject = inventoryArray.getJSONObject(i)
                        val inventory = Inventory(
                            id = inventoryObject.getString("id"),
                            packageId = inventoryObject.getString("packageId"),
                            quantity = inventoryObject.getInt("quantity"),
                            createdAt = inventoryObject.getString("createdAt")
                        )
                        repository.insertInventory(inventory)
                    }
                }
                
                // Import stores
                if (jsonObject.has("stores")) {
                    val storesArray = jsonObject.getJSONArray("stores")
                    for (i in 0 until storesArray.length()) {
                        val storeObject = storesArray.getJSONObject(i)
                        val store = Store(
                            id = storeObject.getString("id"),
                            name = storeObject.getString("name"),
                            priceType = storeObject.getString("priceType"),
                            createdAt = storeObject.getString("createdAt")
                        )
                        repository.insertStore(store)
                    }
                }
                
                // Import expenses
                if (jsonObject.has("expenses")) {
                    val expensesArray = jsonObject.getJSONArray("expenses")
                    for (i in 0 until expensesArray.length()) {
                        val expenseObject = expensesArray.getJSONObject(i)
                        val expense = Expense(
                            id = expenseObject.getString("id"),
                            type = expenseObject.getString("type"),
                            amount = expenseObject.getDouble("amount"),
                            notes = expenseObject.getString("notes"),
                            date = expenseObject.getString("date"),
                            addLater = expenseObject.getBoolean("addLater")
                        )
                        repository.insertExpense(expense)
                    }
                }
                
                // Import sales
                if (jsonObject.has("sales")) {
                    val salesArray = jsonObject.getJSONArray("sales")
                    for (i in 0 until salesArray.length()) {
                        val saleObject = salesArray.getJSONObject(i)
                        val sale = Sale(
                            id = saleObject.getString("id"),
                            storeId = saleObject.getString("storeId"),
                            packageId = saleObject.getString("packageId"),
                            reason = saleObject.getString("reason"),
                            quantity = saleObject.getInt("quantity"),
                            amount = saleObject.getDouble("amount"),
                            pricePerUnit = saleObject.getDouble("pricePerUnit"),
                            total = saleObject.getDouble("total"),
                            date = saleObject.getString("date")
                        )
                        repository.insertSale(sale)
                    }
                }
                
                // Import payments
                if (jsonObject.has("payments")) {
                    val paymentsArray = jsonObject.getJSONArray("payments")
                    for (i in 0 until paymentsArray.length()) {
                        val paymentObject = paymentsArray.getJSONObject(i)
                        val payment = Payment(
                            id = paymentObject.getString("id"),
                            storeId = paymentObject.getString("storeId"),
                            amount = paymentObject.getDouble("amount"),
                            notes = paymentObject.getString("notes"),
                            date = paymentObject.getString("date")
                        )
                        repository.insertPayment(payment)
                    }
                }
                
                _successMessage.value = "تم استيراد البيانات بنجاح"
                
            } catch (e: Exception) {
                _errorMessage.value = "خطأ في استيراد البيانات: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSuccess() {
        _successMessage.value = null
    }
}