package com.example.assignment2.Screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.assignment2.Data.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddProductViewModel:ViewModel(){

    private val _uiState = MutableStateFlow(AddProductUIState())
    private val db = Firebase.firestore
    val uiState: StateFlow<AddProductUIState> = _uiState.asStateFlow()

    var productName by mutableStateOf("")
    var productPrice by mutableStateOf("")
    var productQuantity by mutableStateOf("")

    fun updateProductCategory(newCategory: String) {
        _uiState.update { currentState ->
            currentState.copy(productCategory = newCategory)
        }
    }
    var productPhoto by mutableStateOf<Uri?>(null)

    var productNameError by mutableStateOf<String?>(null)
    var productCategoryError by mutableStateOf<String?>(null)
    var productPriceError by mutableStateOf<String?>(null)
    var productQuantityError by mutableStateOf<String?>(null)
    fun validateInput(): Boolean {
        var isValid = true

        // Validate product name
        if (productName.isBlank()) {
            productNameError = "Product name is required"
            isValid = false
        } else {
            productNameError = null
        }

        // Validate product category
        if (_uiState.value.productCategory.isBlank()) {
            productCategoryError = "Product category is required"
            isValid = false
        } else {
            productCategoryError = null
        }

        // Validate product price
        if (productPrice.isBlank()) {
            productPriceError = "Product price is required"
            isValid = false
        } else {
            try {
                val price = productPrice.toDouble()
                if (price <= 0) {
                    productPriceError = "Product price must be greater than 0"
                    isValid = false
                } else {
                    productPriceError = null
                }
            } catch (e: NumberFormatException) {
                productPriceError = "Invalid price format"
                isValid = false
            }
        }

        // Validate product quantity
        if (productQuantity.isBlank()) {
            productQuantityError = "Product quantity is required"
            isValid = false
        } else {
            try {
                val quantity = productQuantity.toInt()
                if (quantity < 0) {
                    productQuantityError = "Quantity cannot be negative"
                    isValid = false
                } else {
                    productQuantityError = null
                }
            } catch (e: NumberFormatException) {
                productQuantityError = "Invalid quantity format"
                isValid = false
            }
        }

        return isValid
    }



    fun createProduct(context: Context) {
        if (validateInput()) {
            val db = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()
            val productCategory = _uiState.value.productCategory

            productPhoto?.let { photoUri ->
                val storageRef = storage.reference.child("products/${Uri.parse(photoUri.toString()).lastPathSegment}")

                // Upload the photo first
                storageRef.putFile(photoUri).addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()

                        db.collection("Product")
                            .orderBy("productId", com.google.firebase.firestore.Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { documents ->
                                var newProductNumber = 1
                                if (!documents.isEmpty) {
                                    val latestProductId = documents.documents[0].getString("productId")
                                    val latestProductNumber = latestProductId?.substring(1)?.toIntOrNull() ?: 0
                                    newProductNumber = latestProductNumber + 1
                                }
                                val newProductId = "P%03d".format(newProductNumber)

                                val newProduct = Product(
                                    productId = newProductId,
                                    productName = productName,
                                    productCategory = productCategory,
                                    productPrice = productPrice,
                                    productQuantity = productQuantity.toString(),
                                    photo = imageUrl // Store the image URL here
                                )

                                // Add the new product to Firestore
                                db.collection("Product").add(newProduct)
                                    .addOnSuccessListener {
                                       Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Failed to retrieve latest product number", Toast.LENGTH_SHORT).show()
                            }
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}





