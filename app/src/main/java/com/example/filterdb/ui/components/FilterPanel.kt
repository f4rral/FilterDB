package com.example.filterdb.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.filterdb.data.ProductViewModel

@Composable
fun FilterPanel(viewModel: ProductViewModel) {
    val availableFilters by viewModel.availableFilters.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()

    Column {
        // Категории
        DynamicFilterSection(
            title = "Categories",
            allItems = availableFilters.allCategories,
            availableItems = availableFilters.availableCategories,
            selectedItems = currentFilters.selectedCategories,
            onSelectionChanged = viewModel::updateCategories
        )

        // Бренды
        DynamicFilterSection(
            title = "Brands",
            allItems = availableFilters.allBrands,
            availableItems = availableFilters.availableBrands,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DynamicFilterSection(
    title: String,
    allItems: List<String>,
    availableItems: List<String>,
    selectedItems: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)

        FlowRow {
            allItems.forEach { item ->
                val isAvailable = availableItems.contains(item)
                val isSelected = selectedItems.contains(item)

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isAvailable) {
                            val newSelection = selectedItems.toMutableList().apply {
                                if (contains(item)) remove(item) else add(item)
                            }
                            onSelectionChanged(newSelection)
                        }
                    },
                    enabled = isAvailable,
                    colors = FilterChipDefaults.filterChipColors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    ),
                    label = {
                        Text(
                            text = item,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        if (allItems.isEmpty()) {
            Text("No options available")
        }
    }
}

fun Double.format(decimals: Int) = "%.${decimals}f".format(this)