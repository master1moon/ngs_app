package com.ngs.cards775396439.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeId: Long,
    val amount: Double,
    val notes: String = "",
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable