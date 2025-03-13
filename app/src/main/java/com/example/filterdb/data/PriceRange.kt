package com.example.filterdb.data

data class PriceRange(
    val min: Double,
    val max: Double
) {
    fun toClosedRange() = min..max
}