package com.ngs.`775396439`.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ngs.`775396439`.data.dao.*
import com.ngs.`775396439`.data.entity.*

@Database(
    entities = [
        Package::class,
        Inventory::class,
        Store::class,
        Expense::class,
        Sale::class,
        Payment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun packageDao(): PackageDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun storeDao(): StoreDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun saleDao(): SaleDao
    abstract fun paymentDao(): PaymentDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "network_cards_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}