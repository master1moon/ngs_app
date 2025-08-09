package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey
    val id: String,
    val packageId: String,
    val quantity: Int,
    val createdAt: String
)