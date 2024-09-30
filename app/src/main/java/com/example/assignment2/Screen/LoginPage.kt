package com.example.assignment2.Screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.assignment2.R


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavHostController, viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        content = {
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

                    Image(
                        painter = painterResource(id = R.drawable.bloomthis),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .size(200.dp)  // Adjust size as needed
                            .clip(androidx.compose.foundation.shape.CircleShape) // Make the image circular
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
                        onClick = {
                            viewModel.login { role ->
                                viewModel.clearFields()
                                if (role == "admin") {
                                    // Navigate to Admin Profile Page
                                    navController.navigate(FlowerScreen.ProductInventory.name)
                                } else {
                                    // Navigate to User HomePage
                                    navController.navigate(FlowerScreen.FlowerHome.name)
                                }
                            }
                        },
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
    )
}
