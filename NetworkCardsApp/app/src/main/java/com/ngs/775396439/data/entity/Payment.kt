package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey
    val id: String,
    val storeId: String,
    val amount: Double,
    val notes: String?,
    val date: String
)