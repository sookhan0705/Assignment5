package com.example.assignment2.Screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel: ViewModel() {
    private val _products = MutableStateFlow<List<ProductDetailState>>(emptyList())
    val products: StateFlow<List<ProductDetailState>> = _products

    private val _restockQuantity = MutableStateFlow("") // New state for restock quantity
    var restockQuantity: StateFlow<String> = _restockQuantity

    private val _SelectedProduct = MutableStateFlow<ProductDetailState?>(null)
    val selectedProduct: StateFlow<ProductDetailState?> = _SelectedProduct


    private val _productName = MutableStateFlow("")
    var productName: StateFlow<String> = _productName

    private val _productCategory = MutableStateFlow("")
    var productCategory: StateFlow<String> = _productCategory

    private val _productPrice = MutableStateFlow("")
    var productPrice: StateFlow<String> = _productPrice

    private val _productQuantity = MutableStateFlow("")
    var productQuantity: StateFlow<String> = _productQuantity


    private val db = FirebaseFirestore.getInstance()

    init {
        fetchProducts()
    }

    private fun fetchProducts(){
        db.collection("Product")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val productList = snapshot.toObjects(ProductDetailState::class.java)
                    _products.value = productList
                }
            }

    }

    fun getProductById(productId: String) {
        Log.d("ProductDetailViewModel", "Attempting to fetch product with ID: $productId")
        viewModelScope.launch {
            db.collection("Product")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            // Document found, process the data
                            val product = document.toObject(ProductDetailState::class.java)
                            _SelectedProduct.value = product

                            _productName.value = product.productName
                            _productCategory.value = product.productCategory
                            _productPrice.value = product.productPrice
                            _productQuantity.value = product.productQuantity
                        }
                    } else {
                        // No document matches the query
                        Log.d("Firebase", "No document found with productId: $productId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error getting documents: ", exception)
                }
        }
    }

    fun updateProduct(productId: String,context: Context) {
        viewModelScope.launch {
            db.collection("Product")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener {
                    documents->
                    if (!documents.isEmpty) {
                        documents.forEach { document ->
                            val currentQuantity = productQuantity.value.toIntOrNull() ?: 0
                            val restockValue = restockQuantity.value.toIntOrNull() ?: 0
                            val newQuantity = currentQuantity + restockValue

                            val productUpdates = hashMapOf<String, Any>(
                                "productName" to productName.value,
                                "productCategory" to productCategory.value,
                                "productPrice" to productPrice.value,
                                "productQuantity" to newQuantity.toString(),
                                "LastRestock" to restockValue.toString()// Convert new quantity to String
                            )// Update each matching document
                            db.collection("Product").document(document.id)
                                .update(productUpdates)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                                    Log.d("Firebase", "Product updated successfully: ${document.id}")
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
                                    Log.w("Firebase", "Error updating product", e)
                                }
                        }
                    } else {
                        Log.d("Firebase", "No document found with $productId: $productId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error getting documents: ", exception)
                }
        }
    }



    // Functions to update the mutable state values
    fun setProductName(name: String) {
        _productName.value = name
    }

    fun setProductCategory(category: String) {
        _productCategory.value = category
    }

    fun setProductPrice(price: String) {
        _productPrice.value = price
    }

    // Additional method to set the restock quantity
    fun setRestockQuantity(quantity: String) {
        _restockQuantity.value = quantity
    }

}