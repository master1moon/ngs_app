package com.ngs.cards775396439.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "stores")
data class Store(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String = "",
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable