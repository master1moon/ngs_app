package com.ngs.`775396439`.data.dao

import androidx.room.*
import com.ngs.`775396439`.data.entity.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE storeId = :storeId ORDER BY date DESC")
    fun getSalesByStore(storeId: String): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: String): Sale?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale)
    
    @Update
    suspend fun updateSale(sale: Sale)
    
    @Delete
    suspend fun deleteSale(sale: Sale)
    
    @Query("SELECT SUM(total) FROM sales")
    suspend fun getTotalSales(): Double?
    
    @Query("SELECT SUM(total) FROM sales WHERE storeId = :storeId")
    suspend fun getTotalSalesByStore(storeId: String): Double?
    
    @Query("SELECT SUM(total) FROM sales WHERE date BETWEEN :fromDate AND :toDate")
    suspend fun getTotalSalesByDateRange(fromDate: String, toDate: String): Double?
}