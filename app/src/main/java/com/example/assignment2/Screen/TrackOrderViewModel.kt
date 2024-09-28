package com.example.assignment2.Screen

import androidx.lifecycle.ViewModel
import com.example.assignment2.Data.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class TrackOrderViewModel : ViewModel() {
    private val _foundOrder = MutableStateFlow<Order?>(null)
    val foundOrder: StateFlow<Order?> = _foundOrder


    fun searchOrderByOrderId(orderId: String) {
        var db = Firebase.firestore


        db.collection("orders")
            .whereEqualTo("OrderId", orderId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val order = documents.documents[0].toObject(Order::class.java)
                    _foundOrder.value = order
                } else {
                    _foundOrder.value = null // No order found
                }
            }
    }
}

