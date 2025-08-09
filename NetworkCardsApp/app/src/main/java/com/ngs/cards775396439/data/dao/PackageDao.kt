package com.ngs.cards775396439.data.dao

import androidx.room.*
import com.ngs.cards775396439.data.entity.Package
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages ORDER BY createdAt DESC")
    fun getAllPackages(): Flow<List<Package>>
    
    @Query("SELECT * FROM packages WHERE id = :id")
    suspend fun getPackageById(id: Long): Package?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(package_: Package): Long
    
    @Update
    suspend fun updatePackage(package_: Package)
    
    @Delete
    suspend fun deletePackage(package_: Package)
    
    @Query("SELECT * FROM packages WHERE name LIKE '%' || :query || '%'")
    fun searchPackages(query: String): Flow<List<Package>>
}