package com.example.assignment2.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.Data.Product
import com.example.assignment2.Data.ProductRepo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProductViewModel(private val repository: ProductRepo): ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()
    private var _productCount = MutableStateFlow(0)
    val productCount = _productCount.asStateFlow()
    private val _totalStock = MutableStateFlow(0)
    var totalStock = _totalStock.asStateFlow()

    fun getProduct() {
        val db = Firebase.firestore
        db.collection("Product")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle or log error appropriately
                    return@addSnapshotListener
                }

                if (value != null) {
                    _products.value = value.toObjects()
                    countTotalQuantity()

                }
            }

    }

    fun countTotalProducts() {
        viewModelScope.launch {
            try {
                val db = Firebase.firestore
                val count = db.collection("Product").get().await().size()
                _productCount.value = count
            } catch (e: Exception) {
                // Handle or log error appropriately
                _productCount.value = 0 // Reset count on error
            }
        }
    }

    fun countTotalQuantity() {
        viewModelScope.launch {
            var total = 0
            for (product in _products.value){
                total += product.productQuantity.toIntOrNull()?:0

            }
            _totalStock.value=total



        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            // This will run inside a coroutine
            _products.value = repository.getAllProducts()
        }
    }
    fun syncProducts() {
        viewModelScope.launch {
            repository.syncProductsFromFirestore()
        }
    }




    init {
        getProduct()
        countTotalProducts()
        syncProducts()

    }





}