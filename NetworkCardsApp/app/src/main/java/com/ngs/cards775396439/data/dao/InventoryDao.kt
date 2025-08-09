package com.ngs.cards775396439.data.dao

import androidx.room.*
import com.ngs.cards775396439.data.entity.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory ORDER BY createdAt DESC")
    fun getAllInventory(): Flow<List<Inventory>>
    
    @Query("SELECT * FROM inventory WHERE packageId = :packageId")
    suspend fun getInventoryByPackageId(packageId: Long): Inventory?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory): Long
    
    @Update
    suspend fun updateInventory(inventory: Inventory)
    
    @Delete
    suspend fun deleteInventory(inventory: Inventory)
    
    @Query("SELECT SUM(quantity) FROM inventory")
    fun getTotalQuantity(): Flow<Int?>
}