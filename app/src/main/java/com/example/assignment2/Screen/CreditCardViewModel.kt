package com.example.assignment2.Screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.assignment2.Data.CreditCard
import com.example.assignment2.Data.CreditCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch




class CreditCardViewModel(private val repository: CreditCardRepository) : ViewModel() {


    fun insertCreditCard(creditCard: CreditCard, onInsertSuccess: () -> Unit) {
        viewModelScope.launch {
            Log.d("CreditCardViewModel", "Attempting to insert card: $creditCard") // Add this line
            try {
                repository.insertCreditCard(creditCard)
                Log.d("CreditCardViewModel", "Card inserted successfully")
                onInsertSuccess()
            } catch (e: Exception) {
                Log.e("CreditCardViewModel", "Error inserting card: ${e.message}")
            }
        }
    }
    fun getAllCC()=repository.getAllCreditCards().asLiveData(viewModelScope.coroutineContext)




}
