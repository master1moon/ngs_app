package com.ngs.`775396439`.data.dao

import androidx.room.*
import com.ngs.`775396439`.data.entity.Package
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages ORDER BY createdAt DESC")
    fun getAllPackages(): Flow<List<Package>>
    
    @Query("SELECT * FROM packages WHERE id = :id")
    suspend fun getPackageById(id: String): Package?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(package_: Package)
    
    @Update
    suspend fun updatePackage(package_: Package)
    
    @Delete
    suspend fun deletePackage(package_: Package)
    
    @Query("SELECT COUNT(*) FROM packages")
    suspend fun getPackagesCount(): Int
}