package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.assignment2.R

@Composable
fun ResetPasswordScreen(navController: NavHostController, viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDEDED)) // Light pink background
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bloomthis),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(bottom = 20.dp)
                .size(200.dp)  // Adjust size as needed
                .clip(androidx.compose.foundation.shape.CircleShape) // Make the image circular
        )

        // Instruction Text
        Text(
            text = "Please enter your email to reset your password",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email Text Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.setEmail(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        )

        // Reset Button
        Button(
            onClick = {
                viewModel.sendPasswordReset {
                    navController.navigate(FlowerScreen.Login.name)
                }
            },
            shape = RoundedCornerShape(50), // Rounded corners for the button
            modifier = Modifier
                .width(180.dp)
                .padding(top = 16.dp),
            colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
        ) {
            Text(text = "Send", fontSize = 16.sp, color = Color.White)
        }

        // Error Message (if any)
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Spacer between button and back to login
        Spacer(modifier = Modifier.height(16.dp))

        // Back to Login Link
        TextButton(
            onClick = { navController.navigate(FlowerScreen.Login.name) }
        ) {
            Text(
                text = "Remember your password? Back to login",
                color = Color(0xFFFA8E71),
                fontWeight = FontWeight.Medium
            )
        }
    }
}



