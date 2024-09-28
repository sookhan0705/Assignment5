package com.example.assignment2.Screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.Data.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


class AdminOrderDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()


    private val _orderList = MutableStateFlow<List<Order>>(emptyList())
    var orderList: MutableStateFlow<List<Order>> = _orderList


    private val _orderDetail = MutableStateFlow(Order())
    var orderDetail: StateFlow<Order?> = _orderDetail.asStateFlow()


    fun updateOrderStatus(newStatus: String) {
        _orderDetail.update { currentState ->
            currentState.copy(OrderStatus = newStatus)
        }
    }


    fun updateOrderProcessStages(newOrderProcessStages: String) {
        _orderDetail.update { currentState ->
            currentState.copy(OrderProcessStages = newOrderProcessStages)
        }
    }


    private val _selectedOrder =MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder


    private val _orderDateTime = MutableStateFlow("")
    val orderDateTime: MutableStateFlow<String> = _orderDateTime


    private val _orderStatus = MutableStateFlow("")
    var orderStatus: StateFlow<String> = _orderStatus


    private val _orderProcessStages = MutableStateFlow("")
    var orderProcessStages: StateFlow<String> = _orderProcessStages


    private val _totalAmount = MutableStateFlow("")
    var totalAmount: StateFlow<String> = _totalAmount


    private val _customerIdFk = MutableStateFlow("")
    var customerIdFk: StateFlow<String> = _customerIdFk


    init {
        fetchOrder()
    }


    private fun fetchOrder() {
        db.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }


                if (snapshot != null && !snapshot.isEmpty) {
                    val orderList = snapshot.toObjects(Order::class.java)
                    _orderList.value = orderList
                }
            }
    }


    fun fetchOrderById(orderId: String) {
        Log.d("AdminOrderDetailViewModel", "Attempting to fetch order with ID: $orderId")
        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("OrderId", orderId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            // Document found, process the data
                            val order = document.toObject(Order::class.java)
                            _selectedOrder.value = order


                            _orderStatus.value = order.OrderStatus
                            _orderDateTime.value = timestampToString(order.OrderDateTime)
                            _orderProcessStages.value = order.OrderProcessStages
                            _totalAmount.value = order.TotalAmount
                            _customerIdFk.value = order.CustomerIdFk
                        }
                    } else {
                        // No document matches the query
                        Log.d("Firebase", "No document found with orderId: $orderId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error getting documents: ", exception)
                }
        }
    }


    private fun timestampToString(timestamp: Timestamp?): String {
        return timestamp?.let {
            val milliseconds = it.seconds * 1000 + it.nanoseconds / 1000000
            val sdf = SimpleDateFormat("dd MMM yyyy, hh.mm a", Locale.getDefault())
            val netDate = Date(milliseconds)
            val date = sdf.format(netDate)
            Log.d("TAG170", date) // Logging the date for debugging
            date
        } ?: "No Date Available" // Return a default string if timestamp is null
    }


    fun updateOrder(orderId: String) {
        viewModelScope.launch {
            val orderStatus= _orderDetail.value.OrderStatus
            val orderProcessStages = _orderDetail.value.OrderProcessStages


            db.collection("orders")
                .whereEqualTo("OrderId", orderId)
                .get()
                .addOnSuccessListener {
                        documents->
                    if (!documents.isEmpty) {
                        documents.forEach { document ->


                            val orderUpdates = hashMapOf<String, Any>(
                                "OrderStatus" to orderStatus,
                                "OrderProcessStages" to orderProcessStages,
                            )
                            db.collection("orders").document(document.id)
                                .update(orderUpdates)
                                .addOnSuccessListener {
                                    Log.d("Firebase", "Order updated successfully: ${document.id}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firebase", "Error updating order", e)
                                }
                        }
                    } else {
                        Log.d("Firebase", "No document found with $orderId: $orderId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firebase", "Error getting documents: ", exception)
                }
        }
    }
}
