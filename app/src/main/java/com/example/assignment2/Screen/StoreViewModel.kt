package com.example.assignment2.Screen

import android.content.Intent
import androidx.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.example.assignment2.Data.CategoryItems
import com.example.assignment2.Data.ProductItem
import com.example.assignment2.Data.StoreUiState
import com.example.assignment2.MainActivity
import com.example.assignment2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StoreViewModel: ViewModel() {
    // Firebase Authentication and Firestore instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()


    // Manage the combined state for both authentication and store features
    private val _uiState = MutableStateFlow(StoreUiState())
    open val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()
    private var userId: String? = null


    // --------------------- Authentication Methods ----------------------


    // Setters for authentication fields
    fun setEmail(email: String) {
        _uiState.update { currentState -> currentState.copy(email = email, errorMessage = null) }
    }


    fun setFullName(fullName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                fullName = fullName,
                errorMessage = null
            )
        }
    }


    fun setUsername(username: String) {
        _uiState.update { currentState ->
            currentState.copy(
                username = username,
                errorMessage = null
            )
        }
    }


    fun setPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                errorMessage = null
            )
        }
    }


    fun setConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                errorMessage = null
            )
        }
    }


    private fun getNextUserId(onComplete: (String) -> Unit) {
        val userRef = firestore.collection("metadata").document("userIdCounter")


        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                // Document does not exist, so create it with the initial value
                userRef.set(mapOf("lastUserId" to "U0000")).addOnSuccessListener {
                    // After creating the document, start the transaction again
                    runTransactionToGenerateUserId(onComplete)
                }.addOnFailureListener { e ->
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = "Failed to create userIdCounter: ${e.message}")
                    }
                }
            } else {
                // Document exists, run the transaction to update the ID
                runTransactionToGenerateUserId(onComplete)
            }
        }.addOnFailureListener { e ->
            _uiState.update { currentState ->
                currentState.copy(errorMessage = "Failed to retrieve userIdCounter: ${e.message}")
            }
        }
    }


    private fun runTransactionToGenerateUserId(onComplete: (String) -> Unit) {
        val userRef = firestore.collection("metadata").document("userIdCounter")


        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentId = snapshot.getString("lastUserId") ?: "U0000"
            val nextIdNumber = currentId.substring(1).toInt() + 1 // Increment the number part
            val nextUserId = "U" + nextIdNumber.toString().padStart(4, '0') // Format as "U0001"


            transaction.update(userRef, "lastUserId", nextUserId) // Ensure 'lastUserId' is correct field name


            nextUserId // Return the new user ID
        }.addOnSuccessListener { newUserId ->
            onComplete(newUserId) // Pass the new userId to the callback
        }.addOnFailureListener { e ->
            _uiState.update { currentState ->
                currentState.copy(errorMessage = "Failed to generate userId: ${e.message}")
            }
        }
    }






    // Firebase Authentication: User SignUp
    fun signUp(onSuccess: () -> Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword
        val fullName = _uiState.value.fullName
        val username = _uiState.value.username


        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty() || username.isEmpty()) {
            _uiState.update { currentState -> currentState.copy(errorMessage = "All fields must be filled") }
        } else if (password != confirmPassword) {
            _uiState.update { currentState -> currentState.copy(errorMessage = "Passwords do not match") }
        } else {
            // Generate new custom user ID for regular users only
            getNextUserId { newUserId ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                // Save user data with custom userId
                                val userProfile = hashMapOf(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "username" to username,
                                    "userId" to newUserId,  // Assign custom userId
                                    "role" to "user" // Only "user" can sign up
                                )
                                firestore.collection("users").document(it.uid).set(userProfile)
                            }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    email = "",
                                    fullName = "",
                                    username = "",
                                    password = "",
                                    confirmPassword = "",
                                    isLoggedIn = true,
                                    errorMessage = null
                                )
                            }
                            onSuccess()
                        } else {
                            _uiState.update { currentState -> currentState.copy(errorMessage = task.exception?.message) }
                        }
                    }
            }
        }
    }




    // Firebase Authentication: User Login
    fun login(onSuccess: (String) -> Unit) {

        val email = _uiState.value.email
        val password = _uiState.value.password


        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { currentState -> currentState.copy(errorMessage = "Email and password cannot be empty") }
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser


                        user?.let {
                            // Get the user's role from Firestore
                            firestore.collection("users").document(it.uid).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        userId = document.getString("userId")
                                        val role = document.getString("role") ?: "user"
                                        if (role == "admin") {
                                            _uiState.update { currentState ->
                                                currentState.copy(isLoggedIn = true)
                                            }
                                        } else if (role == "user") {
                                            _uiState.update { currentState ->
                                                currentState.copy(isLoggedIn = true)
                                            }
                                        }
                                        onSuccess(role) // Pass the role to the calling function
                                    } else {
                                        _uiState.update { currentState ->
                                            currentState.copy(errorMessage = "User document does not exist")
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    _uiState.update { currentState ->
                                        currentState.copy(errorMessage = "Failed to retrieve role: ${e.message}")
                                    }
                                }
                        }
                    } else {
                        _uiState.update { currentState -> currentState.copy(errorMessage = "Invalid email or password") }
                    }
                }
        }
    }

    fun getUserId(): String? {
        return userId // Return the stored userId
    }







    // Firebase Authentication: Reset Password
    fun sendPasswordReset(onSuccess: () -> Unit) {
        val email = _uiState.value.email


        if (email.isEmpty()) {
            _uiState.update { currentState -> currentState.copy(errorMessage = "Email cannot be empty") }
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                email = "",
                                errorMessage = "Reset email sent!"
                            )
                        }
                        onSuccess()
                    } else {
                        _uiState.update { currentState ->
                            currentState.copy(
                                errorMessage = task.exception?.message
                                    ?: "Error sending reset email"
                            )
                        }
                    }
                }
        }
    }


    // --------------------- Profile Methods ----------------------


    // Update phone number in the profile
    fun updatePhoneNumber(newPhoneNumber: String) {
        _uiState.update { currentState -> currentState.copy(phoneNumber = newPhoneNumber) }
    }


    // Update default address in the profile
    fun updateDefaultAddress(newAddress: String) {
        _uiState.update { currentState -> currentState.copy(defaultAddress = newAddress) }
    }




    // Store user profile in Firestore
    fun updateUserProfile(phoneNumber: String, address: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userProfile = hashMapOf(
                "phoneNumber" to phoneNumber,
                "address" to address
            )


            // Update phoneNumber and address in Firestore
            firestore.collection("users").document(user.uid)
                .set(userProfile, SetOptions.merge())  // Merge with existing data
                .addOnSuccessListener {
                    _uiState.update { currentState ->
                        currentState.copy(
                            phoneNumber = phoneNumber,
                            defaultAddress = address,
                            errorMessage = "Profile updated successfully"
                        )
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = "Error updating profile: $e")
                    }
                }
        }
    }


    fun fetchUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userDoc = firestore.collection("users").document(user.uid)
            userDoc.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    val email = data?.get("email") as? String ?: ""
                    val fullName = data?.get("fullName") as? String ?: ""
                    val username = data?.get("username") as? String ?: ""
                    val phoneNumber = data?.get("phoneNumber") as? String ?: ""
                    val defaultAddress = data?.get("address") as? String ?: ""
                    val profileImageUrl = data?.get("profileImageUrl") as? String ?: ""


                    _uiState.update { currentState ->
                        currentState.copy(
                            email = email,
                            fullName = fullName,
                            username = username,
                            phoneNumber = phoneNumber,
                            defaultAddress = defaultAddress,
                            profileImageUrl = profileImageUrl
                        )
                    }
                }
            }.addOnFailureListener { e ->
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = "Failed to fetch profile: ${e.message}")
                }
            }
        } ?: run {
            _uiState.update { currentState ->
                currentState.copy(errorMessage = "User not authenticated")
            }
        }
    }




    // Upload profile image to Firebase Storage
    fun uploadProfileImage(uri: Uri) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val storageRef = storage.reference.child("profile_Images/${user.uid}.jpg")


            // Upload file to Firebase Storage
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    // Get the download URL
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val profileImageUrl = downloadUri.toString()


                        // Update Firestore with the new profile image URL
                        firestore.collection("users").document(user.uid)
                            .update("profileImageUrl", profileImageUrl)
                            .addOnSuccessListener {
                                // Update the UI state with the new profile image URL
                                _uiState.update { currentState ->
                                    currentState.copy(profileImageUrl = profileImageUrl)
                                }
                            }
                            .addOnFailureListener { e ->
                                // Handle Firestore update failure
                                _uiState.update { currentState ->
                                    currentState.copy(errorMessage = "Failed to update Firestore: ${e.message}")
                                }
                            }
                    }.addOnFailureListener { e ->
                        // Handle failure to get download URL
                        _uiState.update { currentState ->
                            currentState.copy(errorMessage = "Failed to get download URL: ${e.message}")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure to upload file to Firebase Storage
                    _uiState.update { currentState ->
                        currentState.copy(errorMessage = "Failed to upload image: ${e.message}")
                    }
                }
        } ?: run {
            _uiState.update { currentState ->
                currentState.copy(errorMessage = "User not authenticated")
            }
        }
    }






    fun logout() {
        auth.signOut()
        _uiState.update { currentState ->
            currentState.copy(
                email = "",
                fullName = "",
                username = "",
                phoneNumber = "",
                defaultAddress = "",
                profileImageUrl = "",
                errorMessage = null
            )
        }
    }




    // --------------------- Store/Homepage Methods ----------------------


    init {
        fetchProducts() // Fetch products when the ViewModel is initialized
        fetchCategories() // Fetch categories when the ViewModel is initialized
    }


    // Fetch all products from Firestore
    fun fetchProducts() {
        firestore.collection("Product")
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<ProductItem>()
                for (document in result) {
                    val product = document.toObject(ProductItem::class.java)
                    productList.add(product)
                }
                _uiState.update { currentState ->
                    currentState.copy(allProducts = productList, products = productList)
                }
            }
            .addOnFailureListener { exception ->
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = "Error fetching products: ${exception.message}")
                }
            }
    }


    // Fetch categories from Firestore (or use static data as shown below)
    fun fetchCategories() {
        val categories = listOf(
            CategoryItems("All", R.drawable.drawing), // A category for "All" products
            CategoryItems("Basket", R.drawable.drawing),
            CategoryItems("Bouquet", R.drawable.drawing),
            CategoryItems("Gift", R.drawable.drawing)
        )
        _uiState.update { currentState ->
            currentState.copy(categories = categories)
        }
    }


    // Filter products based on the selected category
    fun selectCategory(category: String) {
        val selectedCategory = category ?: "All"  // Assign "All" if category is null


        val filteredProducts = if (selectedCategory == "All") {
            _uiState.value.allProducts  // Show all products if "All" is selected
        } else {
            _uiState.value.allProducts.filter { it.productCategory == selectedCategory }
        }


        _uiState.update { currentState ->
            currentState.copy(
                selectedCategory = selectedCategory,  // Update selected category
                products = filteredProducts  // Update filtered products
            )
        }
    }




    fun setSearchQuery(query: String) {
        _uiState.update { currentState -> currentState.copy(searchQuery = query) }
    }


    fun filterProductsByQuery() {
        val query = _uiState.value.searchQuery.trim()


        val filteredProducts = if (query.isEmpty()) {
            _uiState.value.allProducts // Show all products if the query is empty
        } else {
            _uiState.value.allProducts.filter {
                it.productName.contains(query, ignoreCase = true) || // Match product name
                        it.productCategory.contains(query, ignoreCase = true) // Match product category
            }
        }


        _uiState.update { currentState ->
            currentState.copy(products = filteredProducts)
        }
    }







}