package com.example.assignment2.Data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CreditCardRepository(private val creditCardDb: CreditCardDatabase) {


    suspend fun insertCreditCard(creditCard: CreditCard) {
        Log.d("CreditCardRepository", "Inserting credit card: ${creditCard.cardName}")


        withContext(Dispatchers.IO) {
            creditCardDb.creditCardDao.insertCreditCard(creditCard)
        }
    }


    fun getAllCreditCards() = creditCardDb.creditCardDao.getAllCreditCards()
}
