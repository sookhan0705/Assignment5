package com.example.assignment2.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment2.ui.theme.LightPink
import com.example.assignment2.ui.theme.MidPink
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun PaymentMethodScreen(
    onNextButtonClicked: (String) -> Unit,
    onCancelButtonClicked: () -> Unit,
    navigateToCart: () -> Unit,
    db: FirebaseFirestore,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by rememberSaveable { mutableStateOf("") }
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White) // Set background color to White from the color palette
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Spacer(modifier = Modifier.height(16.dp))


        // Subtitle
        Text(
            text = "Choose your payment method to pay",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Spacer(modifier = Modifier.height(32.dp))


        // Payment option buttons
        PaymentOptionButton(
            text = "Credit Card",
            isSelected = selectedPaymentMethod == "Credit Card",
            onClick = { selectedPaymentMethod = "Credit Card" }
        )


        PaymentOptionButton(
            text = "eWallet",
            isSelected = selectedPaymentMethod == "eWallet",
            onClick = { selectedPaymentMethod = "eWallet" }
        )


        PaymentOptionButton(
            text = "Cash",
            isSelected = selectedPaymentMethod == "Cash",
            onClick = { selectedPaymentMethod = "Cash" }
        )


        Spacer(modifier = Modifier.weight(1f))


        // Cancel and Pay buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Cancel Button
            Button(
                onClick = { showCancelDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Orange), // Use the Orange color for the Cancel button
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Cancel", fontSize = 18.sp, color = White)
            }




            // Pay Button
            Button(
                onClick = {
                    if (selectedPaymentMethod.isNotEmpty()) {
                        savePaymentMethod(db,selectedPaymentMethod)
                        onNextButtonClicked(selectedPaymentMethod)

                    }

                },
                enabled = selectedPaymentMethod.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange), // Use the Orange color for the Pay button
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Pay", fontSize = 18.sp, color = White)
            }
        }


        // Cancel confirmation dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(text = "Confirm want to cancel payment?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        showCancelDialog = false
                        navigateToCart()
                    }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text(text = "No")
                    }
                }
            )
        }
    }
}


@Composable
fun PaymentOptionButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MidPink else LightPink, // Use MidPink for selected and LightPink for unselected
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 18.sp
        )
    }
}


fun savePaymentMethod(db: FirebaseFirestore, paymentMethod: String) {
    val paymentData = hashMapOf(
        "paymentMethod" to paymentMethod
    )


    db.collection("payments")
        .add(paymentData)
        .addOnSuccessListener {
            println("Payment method saved successfully: $paymentMethod")
        }
        .addOnFailureListener { e ->
            println("Error saving payment method: $e")
        }
}




@Preview(showBackground = true)
@Composable
fun PaymentMethodScreenPreview() {
    PaymentMethodScreen(
        onNextButtonClicked = { /* Handle next button click for preview */ },
        onCancelButtonClicked = { /* Handle cancel button click for preview */ },
        navigateToCart = { /* Dummy navigation action for preview */ },
        db = FirebaseFirestore.getInstance() // You can pass a Firestore instance or null for preview
    )
}



