package com.example.assignment2.Data

data class CheckoutItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val totalAmount: Double,
    val deliveryDate: String,
    val deliveryTime: String,
    val message: String
)

