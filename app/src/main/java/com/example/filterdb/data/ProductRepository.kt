package com.example.filterdb.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ProductRepository(private val productDao: ProductDao) {
    fun getFilteredProducts(filters: ProductFilter) = productDao.getFilteredProducts(
        categories = filters.selectedCategories,
        categoriesEmpty = filters.selectedCategories.isEmpty(),
        brands = filters.selectedBrands,
        brandsEmpty = filters.selectedBrands.isEmpty(),
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

    fun getAvailableFilters(filter: ProductFilter): Flow<AvailableFilters> {
        return combine(
            productDao.getAllCategories(),
            productDao.getAllBrands(),
            productDao.getAvailableCategories(
                brands = filter.selectedBrands,
                brandsEmpty = filter.selectedBrands.isEmpty(),
                minPrice = filter.priceRange.start,
                maxPrice = filter.priceRange.endInclusive
            ),
            productDao.getAvailableBrands(
                categories = filter.selectedCategories,
                categoriesEmpty = filter.selectedCategories.isEmpty(),
                minPrice = filter.priceRange.start,
                maxPrice = filter.priceRange.endInclusive
            ),
            productDao.getAvailablePriceRange(
                categories = filter.selectedCategories,
                categoriesEmpty = filter.selectedCategories.isEmpty(),
                brands = filter.selectedBrands,
                brandsEmpty = filter.selectedBrands.isEmpty()
            )
        ) { allCategories, allBrands, availableCats, availableBrands, priceRange ->
            AvailableFilters(
                allCategories = allCategories,
                availableCategories = availableCats,
                allBrands = allBrands,
                availableBrands = availableBrands,
                priceRange = priceRange.toClosedRange()
            )
        }
    }
}

data class ProductFilter(
    val selectedCategories: List<String> = emptyList(),
    val selectedBrands: List<String> = emptyList(),
    val priceRange: ClosedFloatingPointRange<Double> = 0.0..0.0
)

data class AvailableFilters(
    val allCategories: List<String>,
    val availableCategories: List<String>,
    val allBrands: List<String>,
    val availableBrands: List<String>,
    val priceRange: ClosedFloatingPointRange<Double>
)
