package com.example.assignment2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Product")
data class Product(
    @PrimaryKey
    val productId: String = "",
    val productCategory :String="",
    val productName: String = "",
    val productPrice: String = "",
    val productQuantity: String = "",
    val photo: String = "",
    val LastRestock:String="",
)
