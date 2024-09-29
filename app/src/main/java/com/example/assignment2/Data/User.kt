package com.example.assignment2.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userTable")
data class User(
    @PrimaryKey val userId: String,  // Primary key for user (unique ID)
    val email: String,
    val fullName: String,
    val username: String,
    val phoneNumber: String?,
    val address: String?,
    val role: String
)