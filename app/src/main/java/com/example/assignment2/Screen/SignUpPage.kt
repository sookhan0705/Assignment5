package com.example.assignment2.Screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun SignUpScreen(navController: NavHostController, viewModel: StoreViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Add ScrollState for enabling scrolling
    val scrollState = rememberScrollState()

    // Scaffold for layout management and showing snackbars
    val scaffoldState = rememberScaffoldState()

    // Setup Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        viewModel.handleGoogleSignInResult(data, {
            navController.navigate(FlowerScreen.FlowerHome.name) // Navigate on success
        }, { errorMessage ->
            viewModel.setErrorMessage(errorMessage) // Display error
        })
    }

    // Pass launcher to ViewModel
    LaunchedEffect(Unit) {
        viewModel.setGoogleSignInLauncher(googleSignInLauncher)
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
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
                    value = uiState.fullName,
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
                    value = uiState.username,
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

                Spacer(modifier = Modifier.height(16.dp))

                // Error message if any
                if (uiState.errorMessage != null) {
                    Text(text = uiState.errorMessage!!, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sign Up Button
                Button(
                    onClick = {
                        viewModel.signUp {
                            viewModel.clearFields()
                            navController.navigate(FlowerScreen.Login.name) // On success, navigate to Login
                        }
                    },
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "or using:",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center)

                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .size(50.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .clickable {
                            viewModel.googleSignIn() // Trigger Google Sign-In
                        }
                )



            }
        }
    }

    // Show Snackbar for errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }
}
