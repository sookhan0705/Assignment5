package com.example.assignment2.Screen

data class ProductDetailState(
    val productId: String = "",
    val productName: String = "",
    val productCategory: String = "",
    val productPrice: String = "",
    val productQuantity: String = "",
    val photo: String = ""  // Assuming your product has a photo URL

)
