package com.ngs.cards775396439.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageId: Long,
    val quantity: Int,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable