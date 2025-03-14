package com.example.filterdb.data

import androidx.room.ColumnInfo

data class PriceRange(
    @ColumnInfo(name = "minValue") val min: Double,
    @ColumnInfo(name = "maxValue") val max: Double
) {
    fun toClosedRange() = min..max
}