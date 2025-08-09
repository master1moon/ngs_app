package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey
    val id: String,
    val storeId: String,
    val packageId: String?,
    val reason: String?,
    val quantity: Int,
    val amount: Double,
    val pricePerUnit: Double,
    val total: Double,
    val date: String
)