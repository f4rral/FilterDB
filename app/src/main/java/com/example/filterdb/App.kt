package com.example.filterdb

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.filterdb.data.AppDatabase
import com.example.filterdb.data.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App : Application() {
    // Переименовываем свойство
    private val _database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "products.db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        // Добавляем задержку для демонстрации
                        delay(1000)
                        repository.insertSampleData()
                    }
                }
            })
            .build()
    }

    val database: AppDatabase
        get() = _database

    // Делаем свойство приватным
    private val _repository by lazy { ProductRepository(database.productDao()) }

    val repository: ProductRepository
        get() = _repository
}