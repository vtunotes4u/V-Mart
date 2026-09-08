package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryNotes: String = "",
    val orderTimestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String,
    val itemsCount: Int,
    val subtotal: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val paymentMethod: String, // "UPI / QR Code", "Cash on Delivery"
    val paymentStatus: String, // "Pending", "Received"
    val deliveryStatus: String, // "New Order", "Packed", "Out for Delivery", "Delivered"
    val deliveryPersonName: String = "V-Mart Express",
    val paymentReference: String = ""
)
