package com.ngs.cards775396439.data.dao

import androidx.room.*
import com.ngs.cards775396439.data.entity.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY createdAt DESC")
    fun getAllStores(): Flow<List<Store>>
    
    @Query("SELECT * FROM stores WHERE id = :id")
    suspend fun getStoreById(id: Long): Store?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: Store): Long
    
    @Update
    suspend fun updateStore(store: Store)
    
    @Delete
    suspend fun deleteStore(store: Store)
    
    @Query("SELECT * FROM stores WHERE name LIKE '%' || :query || '%'")
    fun searchStores(query: String): Flow<List<Store>>
}