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



class PaymentViewModel : ViewModel() {


    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()


    /**
     * Set the payment method (Credit Card, eWallet, Cash)
     */
    fun setPaymentMethod(method: String) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedPaymentMethod = method)
        }
    }


    /**
     * Set the credit card details
     */
    fun setCardDetails(cardName: String, cardNumber: String, expDate: String, cvv: String) {
        _uiState.update { currentState ->
            currentState.copy(
                cardName = cardName,
                cardNumber = cardNumber,
                expDate = expDate,
                cvv = cvv
            )
        }
    }


    /**
     * Reset the payment data
     */
    fun resetPayment() {
        _uiState.value = PaymentUiState()
    }
}


/**
 * UI State class to hold payment data.
 */
data class PaymentUiState(
    val selectedPaymentMethod: String = "",
    val cardName: String = "",
    val cardNumber: String = "",
    val expDate: String = "",
    val cvv: String = ""
)





