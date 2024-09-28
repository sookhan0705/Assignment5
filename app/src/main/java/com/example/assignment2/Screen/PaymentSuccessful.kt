package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment2.R


@Composable
fun PaymentSuccessfulScreen(
    onDoneButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add success.png image (Make sure the image is available in res/drawable)
        Image(
            painter = painterResource(id = R.drawable.success), // Ensure success.png is in res/drawable
            contentDescription = "Success Icon",
            modifier = Modifier.size(150.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))


        Text(
            text = "Payment Successful!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(16.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))


        // Green-colored button
        Button(
            onClick = onDoneButtonClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50) // Green color
            )
        ) {
            Text(text = "Back to Home")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PaymentSuccessfulScreenPreview() {
    PaymentSuccessfulScreen(
        onDoneButtonClicked = {},
        modifier = Modifier.fillMaxHeight()
    )
}

















