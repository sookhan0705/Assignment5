package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment2.R


@Composable
fun LoginScreen(navController: NavHostController, viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDEDED)), // Light pink background
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
                text = "I'm waiting for you,\nplease enter your details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )


            // Username/Email Field
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


            // Error message if any
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Forgot Password Button
            TextButton(onClick = { navController.navigate(FlowerScreen.ResetPassword.name) }) {
                Text(text = "Forgot Password?", color = Color.Gray)
            }


            // Login Button
            Button(
                onClick = { viewModel.login { navController.navigate(FlowerScreen.FlowerHome.name) } },
                modifier = Modifier
                    .width(180.dp)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
            ) {
                Text(text = "Log in", color = Color.White)
            }


            Spacer(modifier = Modifier.height(16.dp))


            // Sign Up Button
            TextButton(onClick = { navController.navigate(FlowerScreen.SignUp.name) }) {
                Text(text = "Don't have an account? Sign Up", color = Color(0xFFFA8E71))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    val viewModel = StoreViewModel() // Initialize your ViewModel here


    // Provide mock data for the view model if necessary
    viewModel.setEmail("test@example.com")
    viewModel.setPassword("password")


    // Call the LoginScreen with the mocked data
    LoginScreen(navController = navController, viewModel = viewModel)
}


