package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.assignment2.R


@Composable
fun SignUpScreen(navController: NavHostController, viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()


    // Add ScrollState for enabling scrolling
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDEDED)) // Light pink background
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Logo/Image at the top
            Image(
                painter = painterResource(id = R.drawable.drawing), // Replace with your actual logo resource
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp) // Adjust size as needed
                    .padding(bottom = 24.dp) // Space below the logo
            )


            Text(
                text = "Let's create an account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )


            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFA8E71),
                    unfocusedBorderColor = Color.Gray
                )
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Full Name Field
            OutlinedTextField(
                value = uiState.fullName, // Assuming you have added fullName to the state
                onValueChange = { viewModel.setFullName(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFA8E71),
                    unfocusedBorderColor = Color.Gray
                )
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Username Field
            OutlinedTextField(
                value = uiState.username, // Assuming you have added username to the state
                onValueChange = { viewModel.setUsername(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFA8E71),
                    unfocusedBorderColor = Color.Gray
                )
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFA8E71),
                    unfocusedBorderColor = Color.Gray
                )
            )


            Text(
                text = "Must contain a number and at least 6 characters",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            Spacer(modifier = Modifier.height(8.dp))


            // Confirm Password Field
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.setConfirmPassword(it) },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFFA8E71),
                    unfocusedBorderColor = Color.Gray
                )
            )


            Text(
                text = "Must contain a number and at least 6 characters",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            // Error message if any
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Sign Up Button
            Button(
                onClick = { viewModel.signUp { navController.navigate(FlowerScreen.Login.name) } },
                modifier = Modifier
                    .width(180.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
            ) {
                Text(text = "Sign Up", color = Color.White)
            }


            Spacer(modifier = Modifier.height(16.dp))


            // Navigate to Login
            TextButton(onClick = { navController.navigate(FlowerScreen.Login.name) }) {
                Text(text = "Already have an account? Login", color = Color(0xFFFA8E71))
            }
        }
    }
}
