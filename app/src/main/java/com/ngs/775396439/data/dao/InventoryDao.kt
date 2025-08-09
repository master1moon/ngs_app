package com.ngs.`775396439`.data.dao

import androidx.room.*
import com.ngs.`775396439`.data.entity.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    
    @Query("SELECT * FROM inventory ORDER BY createdAt DESC")
    fun getAllInventory(): Flow<List<Inventory>>
    
    @Query("SELECT * FROM inventory WHERE packageId = :packageId")
    fun getInventoryByPackage(packageId: String): Flow<List<Inventory>>
    
    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getInventoryById(id: String): Inventory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory)
    
    @Update
    suspend fun updateInventory(inventory: Inventory)
    
    @Delete
    suspend fun deleteInventory(inventory: Inventory)
    
    @Query("SELECT SUM(quantity) FROM inventory")
    suspend fun getTotalCards(): Int?
    
    @Query("SELECT SUM(quantity) FROM inventory WHERE packageId = :packageId")
    suspend fun getTotalCardsByPackage(packageId: String): Int?
}