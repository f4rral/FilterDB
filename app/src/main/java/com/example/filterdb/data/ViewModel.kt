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

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.getAvailableFilters(ProductFilter()).collect { filters ->
                _filters.update {
                    it.copy(priceRange = filters.priceRange)
                }
            }
        }
    }

    private val _filters = MutableStateFlow(ProductFilter())
    val filters: StateFlow<ProductFilter> = _filters.asStateFlow()

    val availableFilters = _filters
        .flatMapLatest { filters ->
            repository.getAvailableFilters(filters)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AvailableFilters(
                allCategories = emptyList(),
                allBrands = emptyList(),
                availableCategories = emptyList(),
                availableBrands = emptyList(),
                priceRange = 0.0..0.0,
            )
        )

    val products = _filters.flatMapLatest { filters ->
        repository.getFilteredProducts(filters)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
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