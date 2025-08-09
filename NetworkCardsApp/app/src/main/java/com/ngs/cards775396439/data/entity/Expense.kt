package com.ngs.cards775396439.data.entity

data class Expense(
    val id: String,
    val type: String,
    val amount: Double,
    val notes: String = "",
    val date: String,
    val addLater: Boolean = false
)