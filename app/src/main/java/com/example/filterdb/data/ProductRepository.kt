package com.example.filterdb.data

import kotlinx.coroutines.flow.map

class ProductRepository(private val productDao: ProductDao) {
    fun getFilteredProducts(filters: ProductFilters) = productDao.getFilteredProducts(
        categories = filters.selectedCategories,
        brands = filters.selectedBrands,
        minPrice = filters.priceRange.start,
        maxPrice = filters.priceRange.endInclusive
    )

    suspend fun insertSampleData() {
        if (productDao.getProductsCount() == 0) {
            productDao.insertAll(
                listOf(
                    Product(1, "Phone X", "Electronics", "Brand A", 999.99),
                    Product(2, "Laptop Pro", "Electronics", "Brand B", 1499.99),
                    Product(3, "T-Shirt", "Clothing", "Brand C", 29.99),
                    Product(4, "Smart Watch", "Electronics", "Brand D", 199.99),
                    Product(5, "Jeans", "Clothing", "Brand A", 89.99),
                    Product(6, "E-Book Reader", "Electronics", "Brand C", 149.99)
                )
            )
        }
    }

    fun getAvailableCategories() = productDao.getUniqueCategories()
    fun getAvailableBrands() = productDao.getUniqueBrands()
    fun getPriceRange() = productDao.getPriceRange()
        .map { range ->
            if (range.min <= range.max) range else PriceRange(0.0, 0.0)
        }
        .map { it.toClosedRange() }
}