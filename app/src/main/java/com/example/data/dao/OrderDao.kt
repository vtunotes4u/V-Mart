package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderTimestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): Order?

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int

    @Query("SELECT * FROM orders WHERE deliveryStatus != 'Delivered' ORDER BY orderTimestamp DESC")
    fun getActiveOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE deliveryStatus = 'Delivered' ORDER BY orderTimestamp DESC")
    fun getDeliveredOrders(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Order>)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET deliveryStatus = :status WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Long, status: String)

    @Query("UPDATE orders SET paymentStatus = :paymentStatus WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, paymentStatus: String)

    @Delete
    suspend fun deleteOrder(order: Order)
}
