package com.example.assignment2.Data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment2.Screen.FirestoreOrderHistoryViewModel


class OrderHistoryFactory(private val repository: OrderRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirestoreOrderHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirestoreOrderHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewMode class")
    }
}
