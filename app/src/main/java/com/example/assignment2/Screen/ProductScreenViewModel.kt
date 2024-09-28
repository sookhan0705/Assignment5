package com.example.assignment2.Screen

import androidx.lifecycle.ViewModel
import com.example.assignment2.Data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ProductScreenViewModel : ViewModel() {


    // StateFlow to hold products and loading status
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()


    // Function to update products from the composable
    fun updateProducts(productList: List<Product>) {
        _products.update { productList }
    }


    // Function to update loading state
    fun setLoading(loading: Boolean) {
        _loading.update { loading }
    }
}
