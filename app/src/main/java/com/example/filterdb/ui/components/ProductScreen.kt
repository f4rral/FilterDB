package com.example.filterdb.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.filterdb.data.Product
import com.example.filterdb.data.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(viewModel: ProductViewModel) {
    val products by viewModel.products.collectAsState()
    val availableFilters by viewModel.availableFilters.collectAsState()
    val currentFilters by viewModel.filters.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Product Filter") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterPanel(
                viewModel = viewModel,
//                modifier = Modifier.width(300.dp)
            )
            ProductList(products = products)
        }
    }
}

@Composable
fun ProductList(products: List<Product>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(products) {product ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(product.name, style = MaterialTheme.typography.titleSmall)
                    Text("Category: ${product.category}")
                    Text("Brand: ${product.brand}")
                    Text("Price: $${"%.2f".format(product.price)}")
                }
            }
        }
    }
}