package com.example.assignment2.Data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ProductRepo(private val productDao: ProductDao) {

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun syncProductsFromFirestore() {
        val db = Firebase.firestore  // Initialize Firestore
        try {
            val documents = db.collection("Product").get().await()  // Use await() for suspending
            for (document in documents) {
                val product = Product(
                    productId = document.getString("productId") ?: "",
                    productName = document.getString("productName") ?: "",
                    productCategory = document.getString("productCategory") ?: "",
                    productPrice = document.getString("productPrice") ?: "",
                    productQuantity = document.getString("productQuantity") ?: "",
                    LastRestock = document.getString("LastRestock") ?: "",
                    photo = document.getString("photo") ?: ""
                )
                productDao.insertProduct(product) // Pass the product object to insert
            }
        } catch (exception: Exception) {
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }
}
