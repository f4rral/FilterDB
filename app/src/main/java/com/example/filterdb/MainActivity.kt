package com.example.filterdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filterdb.data.ProductViewModel
import com.example.filterdb.ui.components.ProductScreen
import com.example.filterdb.ui.theme.FilterDBTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ProductViewModel> {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductViewModel((application as App).repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilterDBTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProductScreen(viewModel = viewModel)
                }
            }
        }
    }
}