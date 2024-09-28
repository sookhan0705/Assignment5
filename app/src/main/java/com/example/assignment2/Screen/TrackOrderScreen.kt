package com.example.assignment2.Screen

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment2.R
import com.example.assignment2.ui.theme.Black
import com.example.assignment2.ui.theme.DarkGray
import com.example.assignment2.ui.theme.DarkerGray
import com.example.assignment2.ui.theme.Gray
import com.example.assignment2.ui.theme.LightPink
import com.example.assignment2.ui.theme.MidPink
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White

@Composable
fun TrackOrderScreen(
    viewModel: TrackOrderViewModel = viewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var orderId by remember { mutableStateOf("") }
    var searchClicked by remember { mutableStateOf(false) }
    val foundOrder by viewModel.foundOrder.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    Column(modifier = Modifier.background(White).verticalScroll(scrollState)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(200.dp)
                    .width(300.dp)
            ) {
                // OrderId Input
                Text(
                    text = stringResource(R.string.your_order_id).uppercase(),
                    fontWeight = FontWeight.SemiBold,
                )


                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(0.3f))
                    OutlinedTextField(
                        value = orderId,
                        onValueChange = {
                            orderId = it
                            searchClicked = false
                        },
                        label = { Text(text = "Order ID") },
                        placeholder = { Text(text = "999999") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = White,
                            focusedContainerColor = White,
                            unfocusedIndicatorColor = DarkGray,
                            focusedIndicatorColor = MidPink,
                            unfocusedLabelColor = DarkerGray,
                            focusedLabelColor = MidPink,
                            focusedPlaceholderColor = DarkGray
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(0.4f)
                    )


                    Button(
                        onClick = {
                            val clipData = clipboardManager.primaryClip
                            if (clipData != null && clipData.itemCount > 0) {
                                val pastedText = clipData.getItemAt(0).text.toString()
                                orderId = pastedText // Set the pasted text to orderId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent, // No background
                            contentColor = Orange  // Set the text color to orange
                        ),
                        elevation = null, // Remove button elevation
                        modifier = Modifier.weight(0.3f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.paste_order_id),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }


                Button(
                    onClick = {
                        searchClicked = true
                        viewModel.searchOrderByOrderId(orderId)
                    },
                    colors = ButtonDefaults.buttonColors(Orange)
                ) {
                    Text(
                        text = stringResource(id = R.string.check_my_order),
                        modifier = Modifier.padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 4.dp)
                    )
                }
            }


            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = Gray,
            )


            Spacer(modifier = Modifier.height(16.dp))


            if (searchClicked) {
                if (isValidOrderId(orderId)) {
                    foundOrder?.let { order ->
                        val status = order.OrderProcessStages


                        when (status) {
                            "OrderPlaced" -> {
                                TrackOrderSteps(true, false, false, false, false)
                            }
                            "PreppingFlowers" -> {
                                TrackOrderSteps(true, true, false, false, false)
                            }
                            "AwaitingRider" -> {
                                TrackOrderSteps(true, true, true, false, false)
                            }
                            "InDelivery" -> {
                                TrackOrderSteps(true, true, true, true, false)
                            }
                            "Delivered" -> {
                                TrackOrderSteps(true, true, true, true, true)
                            }
                            else -> {
                                Text(text = "Unknown Order Status", color = Color.Red)
                            }
                        }
                    } ?: run {
                        Text(text = "No order found for ID $orderId", color = Color.Gray)
                    }
                } else {
                    Text(text = "Invalid Order ID. \nPlease enter a 6-digit number.", color = Color.Red)
                }
            }
        }
    }
}


private fun isValidOrderId(orderId: String): Boolean {
    return orderId.matches(Regex("^[0-9]{6}$"))
}


@Composable
fun TrackOrderSteps(
    step1: Boolean,
    step2: Boolean,
    step3: Boolean,
    step4: Boolean,
    step5: Boolean,
){
    TrackOrderCard (
        icon = Icons.Default.CheckCircle,
        stepTitle = "Order Placed",
        stepDescription = "We have received your order",
        isCompleted = step1
    )
    TrackOrderCard (
        icon = Icons.Default.CheckCircle,
        stepTitle = "Prepping Flowers",
        stepDescription = "We are preparing your order",
        isCompleted = step2
    )
    TrackOrderCard (
        icon = Icons.Default.CheckCircle,
        stepTitle = "Awaiting Rider",
        stepDescription = "Your order is ready for delivery",
        isCompleted = step3
    )
    TrackOrderCard (
        icon = Icons.Default.CheckCircle,
        stepTitle = "In Delivery",
        stepDescription = "Your order is Out for delivery",
        isCompleted = step4
    )
    TrackOrderCard (
        icon = Icons.Default.CheckCircle,
        stepTitle = "Delivered",
        stepDescription = "Your order is delivered",
        isCompleted = step5,
        isLast = true
    )
}


@Composable
fun TrackOrderCard(
    icon: ImageVector,
    stepTitle: String,
    stepDescription: String,
    isCompleted: Boolean,
    isLast: Boolean = false,
    iconColor: Color = MidPink
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isCompleted) iconColor else DarkGray,
                modifier = Modifier
                    .size(48.dp)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isCompleted) iconColor else DarkGray)
                )
            }
        }


        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stepTitle,
                fontSize = 16.sp,
                color = if (isCompleted) Color.Black else Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stepDescription,
                fontSize = 14.sp
            )
        }
    }
}
