package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val unit: String,
    val iconEmoji: String,
    val inStock: Boolean = true,
    val stockCount: Int = 50,
    val description: String = ""
)
