package com.example.assignment2.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.Data.Order
import com.example.assignment2.Data.OrderRepo
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow


class FirestoreOrderHistoryViewModel(private val repository: OrderRepo) : ViewModel() {


    private var _orderList = MutableStateFlow<List<Order>>(emptyList())
    var orderList = _orderList.asStateFlow()


    private var _filteredOrderList = MutableStateFlow<List<Order>>(emptyList())
    var filteredOrderList = _filteredOrderList.asStateFlow()


    init {
        filterOrders(null)
        getOrderList()
        syncOrder()

    }


    fun getOrderList() {
        val db = Firebase.firestore


        db.collection("orders")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }


                if (value != null) {
                    _orderList.value = value.toObjects(Order::class.java)
                }
            }
    }


    fun syncOrder() {
        viewModelScope.launch {
            repository.syncOrderFromFireStore()
        }
    }


    fun filterOrders(status: String?) {
        _filteredOrderList.value = if (status == null) {
            _orderList.value
        } else {
            _orderList.value.filter { it.OrderStatus == status }
        }
    }
}
