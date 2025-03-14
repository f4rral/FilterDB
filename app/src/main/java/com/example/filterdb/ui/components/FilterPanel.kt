package com.example.filterdb.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.filterdb.data.ProductViewModel

@Composable
fun FilterPanel(viewModel: ProductViewModel) {
    val availableFilters by viewModel.availableFilters.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()

    Column {
        // Категории
        DynamicFilterSection(
            title = "Categories",
            allItems = availableFilters.categories,
            selectedItems = currentFilters.selectedCategories,
            onSelectionChanged = viewModel::updateCategories
        )

        // Бренды
        DynamicFilterSection(
            title = "Brands",
            allItems = availableFilters.brands,
            selectedItems = currentFilters.selectedBrands,
            onSelectionChanged = viewModel::updateBrands
        )

        // Ценовой диапазон
        PriceRangeFilter(
            currentRange = currentFilters.priceRange,
            availableRange = availableFilters.priceRange,
            onRangeChanged = viewModel::updatePriceRange
        )
    }
}

@Composable
private fun PriceRangeFilter(
    currentRange: ClosedFloatingPointRange<Double>,
    availableRange: ClosedFloatingPointRange<Double>,
    onRangeChanged: (ClosedFloatingPointRange<Double>) -> Unit
) {
    val sliderRange = availableRange.start.toFloat()..availableRange.endInclusive.toFloat()
    val currentValues = remember(currentRange) {
        currentRange.start.toFloat()..currentRange.endInclusive.toFloat()
    }

    Column {
        Text("Price Range", style = MaterialTheme.typography.titleSmall)

        RangeSlider(
            value = currentValues,
            onValueChange = { range ->
                onRangeChanged(
                    range.start.toDouble()..range.endInclusive.toDouble()
                )
            },
            valueRange = sliderRange,
            steps = 100
        )

        Text("From: $${currentRange.start.format(2)}")
        Text("To: $${currentRange.endInclusive.format(2)}")
    }
}

@Composable
private fun DynamicFilterSection(
    title: String,
    allItems: List<String>,
    selectedItems: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall)

        if (allItems.isEmpty()) {
            Text("No options available")
        } else {
            allItems.forEach { item ->
                FilterChip(
                    label = { Text(item) },
                    selected = selectedItems.contains(item),
                    onClick = {
                        val newSelection = selectedItems.toMutableList().apply {
                            if (contains(item)) remove(item) else add(item)
                        }
                        onSelectionChanged(newSelection)
                    }
                )
            }
        }
    }
}

fun Double.format(decimals: Int) = "%.${decimals}f".format(this)