package com.example.assignment2.Screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class CartItem(val id: String, val name: String, val price: Double, val quantity: Int)


class CartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()
    fun AddOrder(totalAmount: String, customerIdFk: String) {
        val db = Firebase.firestore


        db.collection("orders")
            .orderBy("OrderId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                var newOrderId = "000001"


                if (!documents.isEmpty) {
                    val latestOrder = documents.documents[0]
                    val latestOrderId = latestOrder.getString("OrderId") ?: "000001"
                    newOrderId = (latestOrderId.toInt() + 1).toString().padStart(6, '0')
                }


                val newOrder = hashMapOf(
                    "OrderId" to newOrderId,
                    "OrderDateTime" to Timestamp.now(),
                    "OrderStatus" to "Active",
                    "OrderProcessStages" to "OrderPlaced",
                    "TotalAmount" to totalAmount,
                    "CustomerIdFk" to customerIdFk
                )


                db.collection("orders")
                    .add(newOrder)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error retrieving latest order", e)
            }
    }

    fun addItemToCart(item: CartItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.cartItems.toMutableList()
            val existingItemIndex = updatedItems.indexOfFirst { it.id == item.id }


            if (existingItemIndex != -1) {
                val existingItem = updatedItems[existingItemIndex]
                updatedItems[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
            } else {
                updatedItems.add(item)
            }


            currentState.copy(cartItems = updatedItems, totalAmount = calculateTotal(updatedItems))
        }
    }


    fun removeItemFromCart(itemId: String) {
        _uiState.update { currentState ->
            val updatedItems = currentState.cartItems.filter { it.id != itemId }
            currentState.copy(cartItems = updatedItems, totalAmount = calculateTotal(updatedItems))
        }
    }


    fun clearCart() {
        _uiState.value = CartUiState()
    }


    private fun calculateTotal(items: List<CartItem>): Double {
        return items.sumOf { it.price * it.quantity }
    }
}


/**
 * UI State class to hold cart data.
 */
data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0
)



