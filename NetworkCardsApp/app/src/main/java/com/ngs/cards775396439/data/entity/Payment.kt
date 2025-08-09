package com.ngs.cards775396439.data.entity

data class Payment(
    val id: String,
    val storeId: String,
    val amount: Double,
    val notes: String = "",
    val date: String
)