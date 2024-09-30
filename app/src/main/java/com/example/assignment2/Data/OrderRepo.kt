package com.example.assignment2.Data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await


class OrderRepo(private val orderDao: OrderDao) {


    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    suspend fun syncOrderFromFireStore(){
        val db = Firebase.firestore
        try {
            val documents = db.collection("orders").get().await()
            for (document in documents){
                val order = Order(
                    OrderId = document.getString("OrderId") ?: "",
                    OrderDateTime= document.getTimestamp("OrderDateTime") ?: Timestamp.now(),
                    OrderStatus= document.getString("OrderStatus") ?: "",
                    TotalAmount= document.getString("TotalAmount") ?: "",
                    OrderProcessStages= document.getString("OrderProcessStages") ?: "",
                    CustomerIdFk= document.getString("CustomerIdFk") ?: "",
                )
                orderDao.insertOrders(order)
            }
        }catch (
            exception:Exception
        ){
            Log.d("FireStore","Error getting document",exception)
        }
    }
}
