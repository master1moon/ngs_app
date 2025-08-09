package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stores")
data class Store(
    @PrimaryKey
    val id: String,
    val name: String,
    val priceType: String, // retail, wholesale, distributor
    val createdAt: String
) : Parcelable