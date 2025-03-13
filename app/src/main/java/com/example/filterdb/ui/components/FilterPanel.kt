package com.example.filterdb.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.filterdb.data.ProductViewModel

@Composable
fun FilterPanel(
    viewModel: ProductViewModel,
    modifier: Modifier = Modifier
) {
    val availableCategories by viewModel.availableCategories.collectAsState()
    val availableBrands by viewModel.availableBrands.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val priceRange by viewModel.priceRange.collectAsState()

    Column(modifier = modifier.padding(8.dp)) {
        Text("Categories", style = MaterialTheme.typography.titleSmall)
        FilterCheckboxGroup(
            items = availableCategories,
            selectedItems = filters.selectedCategories,
            onSelectionChange = viewModel::updateCategories
        )

        Text("Brands", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        FilterCheckboxGroup(
            items = availableBrands,
            selectedItems = filters.selectedBrands,
            onSelectionChange = viewModel::updateBrands
        )

        PriceFilterSection(
            currentRange = filters.priceRange,
            fullRange = priceRange,
            onPriceChange = viewModel::updatePriceRange
        )
    }
}

@Composable
private fun PriceFilterSection(
    currentRange: ClosedFloatingPointRange<Double>,
    fullRange: ClosedFloatingPointRange<Double>,
    onPriceChange: (ClosedFloatingPointRange<Double>) -> Unit
) {
    val fullRangeFloat = remember(fullRange) {
        fullRange.start.toFloat()..fullRange.endInclusive.toFloat()
    }

    val isValidRange = remember(fullRange) {
        fullRange.start <= fullRange.endInclusive
    }

    if (!isValidRange) {
        return  // Не рендерим слайдер если диапазон невалидный
    }

    val sliderRange = remember(currentRange) {
        if (currentRange.start <= currentRange.endInclusive) {
            currentRange.start.toFloat()..currentRange.endInclusive.toFloat()
        } else {
            0f..0f
        }
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Price Range", style = MaterialTheme.typography.titleSmall)
        RangeSlider(
            value = sliderRange,
            onValueChange = { newRange ->
                onPriceChange(newRange.start.toDouble()..newRange.endInclusive.toDouble())
            },
            valueRange = fullRangeFloat,
            steps = 100,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$${"%.2f".format(sliderRange.start)}")
            Text("$${"%.2f".format(sliderRange.endInclusive)}")
        }
    }
}

@Composable
fun FilterCheckboxGroup(
    items: List<String>,
    selectedItems: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    Column {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = selectedItems.contains(item),
                    onCheckedChange = { checked ->
                        val newSelection = selectedItems.toMutableList().apply {
                            if (checked) add(item) else remove(item)
                        }
                        onSelectionChange(newSelection)
                    }
                )
                Text(item)
            }
        }
    }
}