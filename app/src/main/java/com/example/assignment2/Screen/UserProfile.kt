package com.example.assignment2.Screen


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.assignment2.R


@Composable
fun UserProfileScreen(navController: NavHostController, viewModel: StoreViewModel) {
    // Fetch user profile when the screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()  // Fetch the current user's profile
    }
    val uiState by viewModel.uiState.collectAsState()
    // State to control whether the user is in edit mode
    var isEditable by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    // Profile Image Picker
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            viewModel.uploadProfileImage(it) // Upload image to Firebase Storage
        }
    }


    var isHovered by remember { mutableStateOf(false)}


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDEDED))  // Light pink background
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Profile Title
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )


            // Profile Image
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                if (uiState.profileImageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter (uiState.profileImageUrl),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.user), // Default image
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(100.dp)
                    )
                }


                // Button to change profile image with hover effect
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .graphicsLayer(alpha = if (isHovered) 1f else 0f) // Transparency changes based on hover
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHovered = true
                                    tryAwaitRelease()
                                    isHovered = false
                                }
                            )
                        },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
                ) {
                    Text(text = "Change Image", color = Color.White)
                }


            }






            // Username field (non-editable)
            OutlinedTextField(
                value = uiState.username,
                onValueChange = {},
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = false,  // Disable editing for username
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black
                )
            )


            // Email field (non-editable)
            OutlinedTextField(
                value = uiState.email,
                onValueChange = {},
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = false,  // Disable editing for email
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = Color.Gray,
                    disabledTextColor = Color.Black
                )
            )


            // Phone number field (editable)
            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = { if (isEditable) viewModel.updatePhoneNumber(it) },
                label = { Text("Phone Number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = isEditable,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (isEditable) Color(0xFFFA8E71) else Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )




            // Default Address field (editable)
            OutlinedTextField(
                value = uiState.defaultAddress,
                onValueChange = { if (isEditable) viewModel.updateDefaultAddress(it) },
                label = { Text("Default Address") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = isEditable,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (isEditable) Color(0xFFFA8E71) else Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )


            // Manage/Edit Profile Button
            Button(
                onClick = {
                    if (isEditable) {
                        // Save updated profile data
                        viewModel.updateUserProfile(
                            phoneNumber = uiState.phoneNumber,
                            address = uiState.defaultAddress
                        )
                    }
                    isEditable = !isEditable  // Toggle edit mode
                },
                modifier = Modifier
                    .width(180.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
            ) {
                Text(
                    text = if (isEditable) "Save" else "Manage",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }


            // Display success or error message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = if (uiState.errorMessage!!.contains("success")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }


            // Logout Button
            Button(
                onClick = {
                    viewModel.logout()  // Call the logout function in the ViewModel
                    navController.navigate(FlowerScreen.Welcome.name)  // Navigate back to the Welcome screen
                },
                modifier = Modifier
                    .width(180.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF6723))
            ) {
                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }


}








