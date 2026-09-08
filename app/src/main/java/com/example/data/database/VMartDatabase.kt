package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.OrderDao
import com.example.data.dao.ProductDao
import com.example.data.model.Order
import com.example.data.model.Product

@Database(entities = [Product::class, Order::class], version = 1, exportSchema = false)
abstract class VMartDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: VMartDatabase? = null

        fun getDatabase(context: Context): VMartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VMartDatabase::class.java,
                    "vmart_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
