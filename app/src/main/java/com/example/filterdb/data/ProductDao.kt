package com.example.filterdb.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products WHERE " +
            "(:categoriesEmpty = true OR category IN (:categories)) AND " +
            "(:brandsEmpty = true OR brand IN (:brands)) AND " +
            "price BETWEEN :minPrice AND :maxPrice")
    fun getFilteredProducts(
        categories: List<String>,
        categoriesEmpty: Boolean = categories.isEmpty(),
        brands: List<String>,
        brandsEmpty: Boolean = brands.isEmpty(),
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<Product>>

    @Query("SELECT DISTINCT category FROM products")
    fun getUniqueCategories(): Flow<List<String>>

    @Query("SELECT DISTINCT brand FROM products")
    fun getUniqueBrands(): Flow<List<String>>

    @Query("SELECT MIN(price) as min, MAX(price) as max FROM products")
    fun getPriceRange(): Flow<PriceRange>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int
}