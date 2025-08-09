package com.ngs.cards775396439.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeId: Long,
    val packageId: Long,
    val reason: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val totalAmount: Double,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable