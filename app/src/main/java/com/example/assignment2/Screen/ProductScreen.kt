package com.example.assignment2.Screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.assignment2.Data.Product
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun ProductScreen(
    context: Context,
    db: FirebaseFirestore,
    onAddToCart: (Product) -> Unit,
    onNextButtonClicked: () -> Unit
) {
    var products by remember { mutableStateOf(listOf<Product>()) }
    var loading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        db.collection("Product")
            .get()
            .addOnSuccessListener { result ->
                val fetchedProducts = result.map { document ->
                    Product(
                        productId = document.getString("productId") ?: "",
                        productName = document.getString("productName") ?: "",
                        productPrice = document.getString("productPrice") ?: "0",
                        productQuantity = document.getString("productQuantity") ?: "0",
                        productCategory = document.getString("productCategory") ?: "",
                        photo = document.getString("photo") ?: "",
                        LastRestock = document.getString("lastRestock") ?: ""
                    )
                }
                products = fetchedProducts
                loading = false
            }
            .addOnFailureListener { exception ->
                println("Error fetching products: $exception")
                loading = false
            }
    }


    // Display the list of products or a loading indicator
    Column(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products) { product ->
                    var quantity by remember { mutableStateOf(1) }


                    ProductItem(
                        product = product,
                        cartQuantity = quantity,
                        onQuantityChanged = { newQuantity ->
                            quantity = newQuantity
                        },
                        db = db
                    )
                }
            }


            // Button to navigate to Cart or perform next action
            Button(
                onClick = onNextButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Go to Cart")
            }
        }
    }
}


@Composable
fun ProductItem(
    product: Product,
    cartQuantity: Int,
    onQuantityChanged: (Int) -> Unit,
    db: FirebaseFirestore
) {
    var quantity by remember { mutableStateOf(cartQuantity) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Adjust padding for better spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image using a placeholder if photo URL is available
        Image(
            painter = rememberImagePainter(data = product.photo),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )


        Spacer(modifier = Modifier.width(10.dp))


        Column(modifier = Modifier
            .weight(1.5f)
            .padding(horizontal = 4.dp)
            .wrapContentWidth()) {
            // Enhanced product name
            Text(
                text = product.productName,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .widthIn(max = 160.dp) // Set maximum width
            )
            // Enhanced category display
            Text(
                text = "Category: ${product.productCategory}",
                fontSize = 13.sp,
                color = Color.Gray,
                fontStyle = FontStyle.Italic // Italic style for differentiation
            )
            // Enhanced price display
            Text(
                text = "Price: RM${product.productPrice}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium, // Medium weight for visibility
                color = Color.DarkGray // Darker color for emphasis
            )
        }


        // Quantity Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                if (quantity > 1) {
                    val oldQuantity = quantity
                    quantity -= 1
                    onQuantityChanged(quantity)


                    // Update cart and product quantity
                    updateCartItemQuantity(product, quantity, oldQuantity, db)
                }
            }) {
                Text(text = "-", fontSize = 18.sp)
            }
            Text(text = quantity.toString(), fontSize = 16.sp)
            IconButton(onClick = {
                val oldQuantity = quantity
                quantity += 1
                onQuantityChanged(quantity)
            }) {
                Text(text = "+", fontSize = 18.sp)
            }
        }


        Button(onClick = {
            updateCartItemQuantity(product, quantity, cartQuantity, db)
        }) {
            Text("Add to Cart")
        }
    }
}


// Function to update cart and product quantity based on the new cart quantity
fun updateCartItemQuantity(
    product: Product,
    newQuantity: Int,
    oldQuantity: Int, // Track previous quantity to calculate the change
    db: FirebaseFirestore
) {
    // Calculate the quantity difference
    val quantityDifference = newQuantity - oldQuantity


    // Update the product quantity in the database
    updateProductQuantity(product.productId, -quantityDifference, db)


    // Update the cart item with the new quantity
    val cartItemQuery = db.collection("Cart").whereEqualTo("productId", product.productId)


    cartItemQuery.get().addOnSuccessListener { querySnapshot ->
        if (!querySnapshot.isEmpty) {
            for (document in querySnapshot.documents) {
                // Update the cart document with the new quantity
                db.collection("Cart").document(document.id)
                    .update("quantity", newQuantity.toString())
                    .addOnSuccessListener {
                        println("Cart item quantity updated successfully")
                    }
                    .addOnFailureListener { exception ->
                        println("Error updating cart item quantity: $exception")
                    }
            }
        } else {
            // If the product is not in the cart, add it as a new cart item
            val cartItem = hashMapOf(
                "productId" to product.productId,
                "productName" to product.productName,
                "productPrice" to product.productPrice,
                "quantity" to newQuantity.toString(),
                "productCategory" to product.productCategory,
                "photo" to product.photo
            )
            db.collection("Cart")
                .add(cartItem)
                .addOnSuccessListener {
                    println("Product added to cart successfully")
                }
                .addOnFailureListener { exception ->
                    println("Error adding product to cart: $exception")
                }
        }
    }
}


// Function to update the product quantity in Firestore
fun updateProductQuantity(productId: String, quantityChange: Int, db: FirebaseFirestore) {
    val productRef = db.collection("Product").document(productId)


    // Read the current product quantity
    productRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val currentQuantity = document.getString("productQuantity")?.toIntOrNull() ?: 0


                // Update the quantity in Firestore (increase or decrease based on quantityChange)
                productRef.update("productQuantity", FieldValue.increment(quantityChange.toLong()))
                    .addOnSuccessListener {
                        println("Product quantity updated successfully")
                    }
                    .addOnFailureListener { exception ->
                        println("Error updating product quantity: $exception")
                    }
            } else {
                println("No such product document")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting product document: $exception")
        }
}

