package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController



@Composable
fun WelcomeScreen(navController: NavHostController) {
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
            // Logo at the top
            Box(
                modifier = Modifier
                    .padding(bottom=26.dp)
                    .size(250.dp) // Size of the circle
                    .background(
                        color = Color(0xFFFAC8C8).copy(alpha = 0.5f), // Light pink with transparency
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center // Center the text inside the box
            ) {
                Text(
                    text = "Bloomthis", // Text inside the circle
                    fontSize = 45.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }


            // Spacer to separate buttons from the logo
            Spacer(modifier = Modifier.height(16.dp))


            // Sign Up Button
            Button(
                onClick = { navController.navigate(FlowerScreen.SignUp.name) }, // Navigate to Sign Up page
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6723))
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }


            // Login Button
            Button(
                onClick = { navController.navigate(FlowerScreen.Login.name) }, // Navigate to Login page
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6723))
            ) {
                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    val navController = rememberNavController()
    WelcomeScreen(navController = navController)
}
