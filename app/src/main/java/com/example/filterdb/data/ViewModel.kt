package com.example.filterdb.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductFilters(
    val selectedCategories: List<String> = emptyList(),
    val selectedBrands: List<String> = emptyList(),
    val priceRange: ClosedFloatingPointRange<Double> = 0.0..0.0
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _filters = MutableStateFlow(ProductFilters(priceRange = 0.0..0.0))
    val filters: StateFlow<ProductFilters> = _filters.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPriceRange().collect { range ->
                // Обновляем фильтры только при первом получении данных
                if (_filters.value.priceRange.start == 0.0 &&
                    _filters.value.priceRange.endInclusive == 0.0) {
                    _filters.update { it.copy(priceRange = range) }
                }
            }
        }
    }

    val products = _filters.flatMapLatest { filters ->
        repository.getFilteredProducts(filters)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val availableCategories = repository.getAvailableCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableBrands = repository.getAvailableBrands()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val priceRange = repository.getPriceRange()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0..0.0
        )

    fun updateCategories(categories: List<String>) {
        _filters.update { it.copy(selectedCategories = categories) }
    }

    fun updateBrands(brands: List<String>) {
        _filters.update { it.copy(selectedBrands = brands) }
    }

    fun updatePriceRange(range: ClosedFloatingPointRange<Double>) {
        _filters.update { it.copy(priceRange = range) }
    }
}