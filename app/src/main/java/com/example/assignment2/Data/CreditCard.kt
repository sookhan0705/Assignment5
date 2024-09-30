package com.example.assignment2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey




@Entity(tableName = "credit_cards")
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardName: String,
    val cardNumber: String,
    val expDate: String,
    val cvv: String
)

