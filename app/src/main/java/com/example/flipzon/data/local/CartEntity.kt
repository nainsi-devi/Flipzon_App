package com.example.flipzon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
)
