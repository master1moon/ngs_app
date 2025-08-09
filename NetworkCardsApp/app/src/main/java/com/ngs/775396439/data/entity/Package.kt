package com.ngs.`775396439`.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "packages")
data class Package(
    @PrimaryKey
    val id: String,
    val name: String,
    val retailPrice: Double?,
    val wholesalePrice: Double?,
    val distributorPrice: Double?,
    val createdAt: String,
    val image: String
) : Parcelable