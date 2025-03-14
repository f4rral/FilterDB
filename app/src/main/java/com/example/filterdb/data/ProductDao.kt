package com.example.filterdb.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Основной запрос с фильтрацией
    @Query("""
        SELECT * FROM products 
        WHERE 
            (:categoriesEmpty OR category IN (:categories)) AND 
            (:brandsEmpty OR brand IN (:brands)) AND 
            price BETWEEN :minPrice AND :maxPrice
    """)
    fun getFilteredProducts(
        categories: List<String>,
        categoriesEmpty: Boolean = categories.isEmpty(),
        brands: List<String>,
        brandsEmpty: Boolean = brands.isEmpty(),
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<Product>>

    @Insert
    suspend fun insertAll(products: List<Product>)

    // Динамические доступные фильтры
    @Query("""
        SELECT DISTINCT category FROM products 
        WHERE 
            (:brandsEmpty OR brand IN (:brands)) AND 
            price BETWEEN :minPrice AND :maxPrice
    """)
    fun getAvailableCategories(
        brands: List<String>,
        brandsEmpty: Boolean,
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<String>>

    @Query("""
        SELECT DISTINCT brand FROM products 
        WHERE 
            (:categoriesEmpty OR category IN (:categories)) AND 
            price BETWEEN :minPrice AND :maxPrice
    """)
    fun getAvailableBrands(
        categories: List<String>,
        categoriesEmpty: Boolean,
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<String>>

    @Query("""
        SELECT MIN(price) AS minValue, MAX(price) AS maxValue FROM products 
        WHERE 
            (:categoriesEmpty OR category IN (:categories)) AND 
            (:brandsEmpty OR brand IN (:brands))
    """)
    fun getAvailablePriceRange(
        categories: List<String>,
        categoriesEmpty: Boolean,
        brands: List<String>,
        brandsEmpty: Boolean
    ): Flow<PriceRange>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int
}