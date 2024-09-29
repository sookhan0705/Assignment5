package com.example.assignment2.Data

data class ProductItem(
    val productId: String = "",
    val productName: String = "",
    val productPrice: String = "",
    val productCategory: String = "",
    val photo: String = ""
)


data class CategoryItems(
    val name: String,
    val picUrl: Int
)


/** Data class for both store and authentication states */
data class StoreUiState(
    // Authentication related states
    val email: String = "",
    val fullName: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
    val resetSuccess: Boolean = false,


    // Store (Homepage) related states
    val isLoading: Boolean = false,
    val selectedCategory: String = "All",
    val categories: List<CategoryItems> = listOf(),
    val allProducts: List<ProductItem> = listOf(), // All products from Firestore
    val products: List<ProductItem> = listOf(),    // Filtered products
    val searchQuery: String = "",
    val bannerText: String = "Artisan flowers \n& gifts for \nall occasions",






    // Profile related states
    val customUserId: String = "",
    val phoneNumber: String = "",
    val defaultAddress: String = "",
    val profileImageUrl: String = "",
)


