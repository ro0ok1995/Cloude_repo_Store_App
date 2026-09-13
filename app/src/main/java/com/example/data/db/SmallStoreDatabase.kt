package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CustomerEntity::class,
        ProductEntity::class,
        TransactionEntity::class,
        TransactionItemLineEntity::class,
        NotificationEntity::class,
        StoreInfoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmallStoreDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionItemLineDao(): TransactionItemLineDao
    abstract fun notificationDao(): NotificationDao
    abstract fun storeInfoDao(): StoreInfoDao

    companion object {
        @Volatile
        private var INSTANCE: SmallStoreDatabase? = null

        fun getDatabase(context: Context): SmallStoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmallStoreDatabase::class.java,
                    "smallstore_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
