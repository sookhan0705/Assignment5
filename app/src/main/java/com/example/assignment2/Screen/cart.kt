package com.example.assignment2.Screen
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete // Import for trash icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import coil.compose.rememberImagePainter
import com.example.assignment2.Data.CheckoutItem
import com.example.assignment2.Data.Product
import com.example.assignment2.ui.theme.Orange

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CartScreen(context: Context, db: FirebaseFirestore, onCheckoutClicked: () -> Unit,
               viewModel: CartViewModel = viewModel(),
               userViewModel:StoreViewModel) {
    var cartItems by remember { mutableStateOf(listOf<Product>()) }
    var loading by remember { mutableStateOf(true) }
    var totalAmount by remember { mutableStateOf(0.0) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedDeliveryTime by remember { mutableStateOf("MORNING\n9AM - 2PM") }
    var message by remember { mutableStateOf("") }
    var userId= userViewModel.getUserId()

    // Fetch cart items from Firestore's Cart collection
    LaunchedEffect(Unit) {
        db.collection("Cart")
            .get()
            .addOnSuccessListener { result ->
                val fetchedCartItems = result.map { document ->
                    val productPrice = document.getString("productPrice")?.toDoubleOrNull() ?: 0.0
                    val quantity = document.getString("quantity")?.toIntOrNull() ?: 1
                    totalAmount += productPrice * quantity


                    Product(
                        productId = document.getString("productId") ?: "",
                        productName = document.getString("productName") ?: "",
                        productPrice = document.getString("productPrice") ?: "0",
                        productQuantity = quantity.toString(), // Keep the quantity as string
                        productCategory = document.getString("productCategory") ?: "",
                        photo = document.getString("photo") ?: "",
                        LastRestock = ""
                    )
                }
                cartItems = fetchedCartItems
                loading = false
            }
            .addOnFailureListener { exception ->
                println("Error fetching cart items: $exception")
                loading = false
            }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        // Display loading indicator while cart data is being fetched
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Show list of cart items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Allow scrolling if needed
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { cartItem ->
                    CartItemDisplay(cartItem, db, { updatedItem ->
                        // Update the cart items list and Firestore when quantity changes
                        cartItems = cartItems.map {
                            if (it.productId == updatedItem.productId) updatedItem else it
                        }
                        totalAmount = calculateTotalAmount(cartItems) // Update total amount
                        updateCartItemQuantity(db, updatedItem) // Update Firestore with new quantity
                    }) { productId ->
                        // Handle item removal
                        removeFromCart(db, productId) { isDeleted ->
                            if (isDeleted) {
                                cartItems = cartItems.filter { it.productId != productId } // Update the cart items list
                                totalAmount = calculateTotalAmount(cartItems) // Recalculate total amount
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            // Delivery Date Picker
            DeliveryDatePicker(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                context = context
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Delivery Time Selector
            DeliveryTimeSelector(
                deliveryTimes = listOf(
                    "MORNING\n9AM - 2PM",
                    "STANDARD\n9AM - 6PM",
                    "EVENING\n6PM - 8PM"
                ),
                selectedDeliveryTime = selectedDeliveryTime,
                onTimeSelected = { selectedDeliveryTime = it }
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Write Message
            WriteMessage(
                message = message,
                onMessageChange = { message = it }
            )


            Spacer(modifier = Modifier.height(16.dp))


            // Checkout Section with the total amount
            CheckoutSection(
                totalAmount = "RM${"%.2f".format(totalAmount)}",
                onCheckoutClicked = {
                    viewModel.AddOrder(totalAmount.toString(),userId.toString())
                    // Prepare checkout items
                    val checkoutItems = cartItems.map { cartItem ->
                        CheckoutItem(
                            productId = cartItem.productId,
                            productName = cartItem.productName,
                            quantity = cartItem.productQuantity.toInt(),
                            totalAmount = (cartItem.productPrice.toDoubleOrNull() ?: 0.0) * (cartItem.productQuantity.toInt()),
                            deliveryDate = selectedDate,
                            deliveryTime = selectedDeliveryTime,
                            message = message
                        )
                    }
                    // Save checkout info
                    saveCheckoutInfo(db, checkoutItems, totalAmount)
                    onCheckoutClicked()
                }
            )
        }
    }
}


// Function to calculate the total amount based on the current cart items
fun calculateTotalAmount(cartItems: List<Product>): Double {
    return cartItems.sumOf { (it.productPrice.toDoubleOrNull() ?: 0.0) * (it.productQuantity.toIntOrNull() ?: 1) }
}


// Function to save checkout info
fun saveCheckoutInfo(db: FirebaseFirestore, checkoutItems: List<CheckoutItem>, totalAmount: Double) {
    val checkoutData = hashMapOf(
        "checkoutItems" to checkoutItems,
        "totalAmount" to totalAmount
    )


    db.collection("CheckOutCart") // Save in "CheckOutCart" collection
        .add(checkoutData)
        .addOnSuccessListener {
            println("Checkout info saved successfully.")
        }
        .addOnFailureListener { e ->
            println("Error saving checkout info: $e")
        }
}


// Function to update the cart item quantity in Firestore
fun updateCartItemQuantity(db: FirebaseFirestore, updatedItem: Product) {
    db.collection("Cart")
        .whereEqualTo("productId", updatedItem.productId) // Find the document to update
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection("Cart").document(document.id).update("quantity", updatedItem.productQuantity)
                    .addOnSuccessListener {
                        println("Cart item quantity updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        println("Error updating item quantity: $e")
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error finding item to update: $e")
        }
}


@Composable
fun CartItemDisplay(cartItem: Product, db: FirebaseFirestore, onQuantityChanged: (Product) -> Unit, onItemRemoved: (String) -> Unit) {
    var quantity by remember { mutableStateOf(cartItem.productQuantity.toInt()) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image (optional)
        Image(
            painter = rememberImagePainter(data = cartItem.photo),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )


        Spacer(modifier = Modifier.width(16.dp))


        // Product details
        Column(modifier = Modifier.weight(1f)) {
            Text(text = cartItem.productName, fontSize = 16.sp, color = Color.Black)
            Text(text = "Category: ${cartItem.productCategory}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "RM${cartItem.productPrice} x $quantity", fontSize = 14.sp, color = Color.Black)
        }


        // Quantity Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (quantity > 1) {
                quantity--
                onQuantityChanged(cartItem.copy(productQuantity = quantity.toString())) // Update quantity
            } }) {
                Text(text = "-", fontSize = 18.sp)
            }
            Text(text = quantity.toString(), fontSize = 16.sp)
            IconButton(onClick = {
                quantity++
                onQuantityChanged(cartItem.copy(productQuantity = quantity.toString())) // Update quantity
            }) {
                Text(text = "+", fontSize = 18.sp)
            }
            IconButton(onClick = {
                // Remove item from cart when trash icon is clicked
                removeFromCart(db, cartItem.productId) { isDeleted ->
                    if (isDeleted) {
                        onItemRemoved(cartItem.productId) // Notify the item was removed
                    }
                }
            }) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red) // Use trash icon
            }
        }
    }
}


// Function to remove an item from the cart
fun removeFromCart(db: FirebaseFirestore, productId: String, onItemRemoved: (Boolean) -> Unit) {
    db.collection("Cart")
        .whereEqualTo("productId", productId) // Find the document to delete
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection("Cart").document(document.id).delete()
                    .addOnSuccessListener {
                        println("Item removed successfully.")
                        onItemRemoved(true) // Notify that the item was removed
                    }
                    .addOnFailureListener { e ->
                        println("Error removing item: $e")
                        onItemRemoved(false) // Notify that there was an error
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error finding item to remove: $e")
            onItemRemoved(false) // Notify that there was an error
        }
}


@Composable
fun CheckoutSection(totalAmount: String, onCheckoutClicked: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total", fontSize = 18.sp, color = Color.Black)
            Text(text = totalAmount, fontSize = 18.sp, color = Color.Black)
        }


        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = onCheckoutClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange)
        ) {
            Text(text = "CHECK OUT", color = Color.White, fontSize = 18.sp)
        }
    }
}


@Composable
fun WriteMessage(message: String, onMessageChange: (String) -> Unit) {
    OutlinedTextField(
        value = message,
        onValueChange = onMessageChange,
        label = { Text("Enter your message") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}


@Composable
fun DeliveryDatePicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    context: Context
) {
    val calendar = Calendar.getInstance()
    var date by remember { mutableStateOf(selectedDate) }


    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date = "$dayOfMonth/${month + 1}/$year"
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    OutlinedTextField(
        value = if (date.isEmpty()) "Select Date" else date,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        label = { Text("Delivery Date") }
    )
}


@Composable
fun DeliveryTimeSelector(
    deliveryTimes: List<String>,
    selectedDeliveryTime: String,
    onTimeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        deliveryTimes.forEach { time ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clickable { onTimeSelected(time) }
                    .background(
                        if (time == selectedDeliveryTime) Orange else Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (time == selectedDeliveryTime) Color.Black else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = time,
                    textAlign = TextAlign.Center,
                    color = if (time == selectedDeliveryTime) Color.Black else Color.DarkGray
                )
            }
        }
    }
}





