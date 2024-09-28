package com.example.assignment2.Data

data class CartData(
    val productId: String,
    val name: String,
    val price: String,
    var quantity: Int,
    val deliveryDate: String,
    val message: String,
    val deliveryTime: String
)


object CartManager {
    var items: MutableList<CartData> = mutableListOf()


    fun addToCart(product: Product, quantity: Int, selectedDate: String, selectedDeliveryTime: String, message: String) {
        val existingItem = items.find { it.productId == product.productId }
        if (existingItem != null) {
            existingItem.quantity += quantity // Update existing item quantity
        } else {
            items.add(CartData(product.productId, product.productName, product.productPrice, quantity, selectedDate, message, selectedDeliveryTime))
        }
    }


    fun clearCart() {
        items.clear()
    }


    fun getTotalAmount(): Double {
        return items.sumOf { it.price.toDoubleOrNull() ?: 0.0 * it.quantity }
    }
}





