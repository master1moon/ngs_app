package com.ngs.cards775396439.data.entity

data class Package(
    val id: String,
    val name: String,
    val retailPrice: Double? = null,
    val wholesalePrice: Double? = null,
    val distributorPrice: Double? = null,
    val createdAt: String,
    val image: String = ""
)