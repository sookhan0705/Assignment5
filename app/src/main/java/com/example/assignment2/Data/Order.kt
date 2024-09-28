package com.example.assignment2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp


@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val OrderId: String = "",
    val OrderDateTime: Timestamp = Timestamp.now(),
    val OrderStatus: String = "",
    val TotalAmount: String = "",
    val OrderProcessStages: String = "",
    val CustomerIdFk: String = ""
)
