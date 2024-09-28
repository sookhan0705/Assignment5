package com.example.assignment2.Screen

import androidx.lifecycle.ViewModel
import com.example.assignment2.Data.Order
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class OrderManagementViewModel : ViewModel() {
    private var _orderList = MutableStateFlow<List<Order>>(emptyList())
    var orderList = _orderList.asStateFlow()


    private var _filteredOrderList = MutableStateFlow<List<Order>>(emptyList())
    var filteredOrderList = _filteredOrderList.asStateFlow()


    init {
        getOrderList()
    }


    private fun getOrderList() {
        var db = Firebase.firestore


        db.collection("orders")
            .orderBy("OrderDateTime", Query.Direction.DESCENDING) // Sort by descending order
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }


                if (value != null) {
                    _orderList.value = value.toObjects(Order::class.java)
                    _filteredOrderList.value = _orderList.value // Initially show all orders
                }
            }


    }


    fun filterOrders(status: String?) {
        _filteredOrderList.value = if (status == null) {
            _orderList.value // Show all orders
        } else {
            _orderList.value.filter { it.OrderStatus == status }
        }
    }
}
