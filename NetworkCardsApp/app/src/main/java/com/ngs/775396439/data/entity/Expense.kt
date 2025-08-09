package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey
    val id: String,
    val type: String,
    val amount: Double,
    val notes: String?,
    val date: String,
    val addLater: Boolean = false
)