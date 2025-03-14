package com.example.filterdb.data

import kotlinx.coroutines.flow.combine

class ProductRepository(private val productDao: ProductDao) {
    fun getFilteredProducts(filters: ProductFilters) = productDao.getFilteredProducts(
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

    fun getAllCategories() = productDao.getAllCategories()
    fun getAllBrands() = productDao.getAllBrands()

    fun getAvailableFilters(filters: ProductFilters) = combine(
        getAllCategories(),
        getAllBrands(),

        productDao.getAvailableCategories(
            brands = filters.selectedBrands,
            brandsEmpty = filters.selectedBrands.isEmpty(),
            minPrice = filters.priceRange.start,
            maxPrice = filters.priceRange.endInclusive
        ),
        productDao.getAvailableBrands(
            categories = filters.selectedCategories,
            categoriesEmpty = filters.selectedCategories.isEmpty(),
            minPrice = filters.priceRange.start,
            maxPrice = filters.priceRange.endInclusive
        ),
        productDao.getAvailablePriceRange(
            categories = filters.selectedCategories,
            categoriesEmpty = filters.selectedCategories.isEmpty(),
            brands = filters.selectedBrands,
            brandsEmpty = filters.selectedBrands.isEmpty()
        )
    ) { allCats, allBrands, availableCats, availableBrands, priceRange ->
        AvailableFilters(
            allCategories = allCats,
            availableCategories = availableCats,
            allBrands = allBrands,
            availableBrands = availableBrands,
            priceRange = priceRange.toClosedRange()
        )
    }
}

data class AvailableFilters(
    val allCategories: List<String>,
    val availableCategories: List<String>,
    val allBrands: List<String>,
    val availableBrands: List<String>,
    val priceRange: ClosedFloatingPointRange<Double>
)