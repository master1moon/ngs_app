package com.ngs.cards775396439.data.entity

data class Sale(
    val id: String,
    val storeId: String,
    val packageId: String,
    val reason: String,
    val quantity: Int,
    val amount: Double,
    val pricePerUnit: Double,
    val total: Double,
    val date: String
)