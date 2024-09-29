package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.assignment2.R


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
            Image(
                painter = painterResource(id = R.drawable.bloomthis),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .size(300.dp)  // Adjust size as needed
                    .clip(androidx.compose.foundation.shape.CircleShape) // Make the image circular
            )


            // Sign Up Button
            Button(
                onClick = { navController.navigate(FlowerScreen.SignUp.name) }, // Navigate to Sign Up page
                modifier = Modifier
                    .width(250.dp)
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
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
                    .width(250.dp)
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
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