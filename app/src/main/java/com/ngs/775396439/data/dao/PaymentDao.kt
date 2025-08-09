package com.ngs.`775396439`.data.dao

import androidx.room.*
import com.ngs.`775396439`.data.entity.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE storeId = :storeId ORDER BY date DESC")
    fun getPaymentsByStore(storeId: String): Flow<List<Payment>>
    
    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun getPaymentById(id: String): Payment?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment)
    
    @Update
    suspend fun updatePayment(payment: Payment)
    
    @Delete
    suspend fun deletePayment(payment: Payment)
    
    @Query("SELECT SUM(amount) FROM payments")
    suspend fun getTotalPayments(): Double?
}