package com.example.assignment2.Screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import com.example.assignment2.Data.CreditCard
import com.example.assignment2.R
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun CreditCardScreen(
    db: FirebaseFirestore,
    viewModel: CreditCardViewModel,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var name by rememberSaveable { mutableStateOf("") }
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var expDate by rememberSaveable { mutableStateOf("") }
    var cvv by rememberSaveable { mutableStateOf("") }


    // Validation error states
    var cardNumberError by remember { mutableStateOf(false) }
    var expDateError by remember { mutableStateOf(false) }
    var cvvError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(White)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Payment",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )


        // Credit Card Heading
        Text(
            text = "Credit Card",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        // Card images...
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(painter = painterResource(id = R.drawable.master), contentDescription = "MasterCard", modifier = Modifier.size(100.dp))
            Image(painter = painterResource(id = R.drawable.visa), contentDescription = "Visa", modifier = Modifier.padding(end = 40.dp).size(100.dp))
        }


        Spacer(modifier = Modifier.height(24.dp))


        // Name on card
        if (nameError) {
            Text("Name cannot contain numbers or special characters", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 4.dp))
        }
        OutlinedTextField(
            value = name,
            onValueChange = { newName ->
                name = newName
                nameError = !validateName(newName)
            },
            label = { Text("Name on Card") },
            placeholder = { Text("John Doe") },
            isError = nameError,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),


            )


        // Card number
        if (cardNumberError) {
            Text("Invalid card number, must be 16 digits", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 1.dp))
        }
        OutlinedTextField(
            value = cardNumber,
            onValueChange = { newCardNumber ->
                if (newCardNumber.filter { it.isDigit() }.length <= 16) {
                    cardNumber = formatCardNumber(newCardNumber)
                }
                cardNumberError = !validateCardNumber(cardNumber)
            },
            label = { Text("Card Number") },
            placeholder = { Text("XXXX-XXXX-XXXX-XXXX") },
            isError = cardNumberError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )


        // Expiration date and CVV
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Expiration Date
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                if (expDateError) {
                    Text("Invalid expiration date", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp, bottom = 1.dp))
                }
                OutlinedTextField(
                    value = expDate,
                    onValueChange = { newExpDate ->
                        if (newExpDate.filter { it.isDigit() }.length <= 4) {
                            expDate = formatExpirationDate(newExpDate)
                        }
                        expDateError = !validateExpirationDate(expDate)
                    },
                    label = { Text("Exp Date") },
                    placeholder = { Text("MM/YY") },
                    isError = expDateError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                )
            }


            // CVV
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                if (cvvError) {
                    Text("must be 3 digits", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp, bottom = 1.dp))
                }
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { newCVV ->
                        if (newCVV.filter { it.isDigit() }.length <= 3) {
                            cvv = newCVV
                        }
                        cvvError = !validateCVV(cvv)
                    },
                    label = { Text("CVV") },
                    placeholder = { Text("XXX") },
                    isError = cvvError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))


        // Pay button
        Button(
            onClick = {
                // Validate all fields on button click
                cardNumberError = !validateCardNumber(cardNumber)
                expDateError = !validateExpirationDate(expDate)
                cvvError = !validateCVV(cvv)
                nameError = !validateName(name)


                val newCard = CreditCard(
                    cardName = name,  // Assuming `name` is defined in your state
                    cardNumber = cardNumber,  // Assuming `cardNumber` is defined in your state
                    expDate = expDate,  // Assuming `expDate` is defined in your state
                    cvv = cvv  // Assuming `cvv` is defined in your state
                )
                viewModel.insertCreditCard(newCard){
                    Log.d("CreditCardScreen", "Inserted card successfully.")
                    onNextButtonClicked()
                }
                if (!cardNumberError && !expDateError && !cvvError && !nameError) {
                    saveCreditCardInfo(db, name, cardNumber, expDate, cvv)
                    // Save to Firestore
                    onNextButtonClicked()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 24.dp)
        ) {
            Text(text = "Pay", fontSize = 18.sp, color = Color.White)
        }
    }
}


fun saveCreditCardInfo(db: FirebaseFirestore, cardName: String, cardNumber: String, expDate: String, cvv: String) {
    val cardInfo = hashMapOf(
        "CardName" to cardName,
        "cardNumber" to cardNumber,
        "cvv" to cvv,
        "expDate" to expDate
    )


    db.collection("creditCards")
        .add(cardInfo)
        .addOnSuccessListener {
            println("Credit card info saved successfully.")
        }
        .addOnFailureListener { e ->
            println("Error saving credit card info: $e")
        }
}


// Utility functions remain the same
fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }
    return digits.chunked(4).joinToString("-")
}


fun formatExpirationDate(input: String): String {
    val digits = input.filter { it.isDigit() }
    return if (digits.length >= 2) {
        digits.take(2) + "/" + digits.drop(2).take(2)
    } else {
        digits
    }
}


fun validateCardNumber(cardNumber: String): Boolean {
    return cardNumber.filter { it.isDigit() }.length == 16
}


fun validateExpirationDate(expDate: String): Boolean {
    val parts = expDate.split("/")
    return parts.size == 2 && parts[0].length == 2 && parts[1].length == 2
}


fun validateCVV(cvv: String): Boolean {
    return cvv.length == 3 && cvv.all { it.isDigit() }
}


fun validateName(name: String): Boolean {
    return name.all { it.isLetter() || it.isWhitespace() }
}






