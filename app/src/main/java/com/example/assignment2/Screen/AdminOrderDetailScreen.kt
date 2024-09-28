package com.example.assignment2.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment2.R
import com.example.assignment2.ui.theme.AlmostBlack
import com.example.assignment2.ui.theme.Black
import com.example.assignment2.ui.theme.ColorError
import com.example.assignment2.ui.theme.ColorInfo
import com.example.assignment2.ui.theme.ColorSuccess
import com.example.assignment2.ui.theme.DarkGray
import com.example.assignment2.ui.theme.DarkerGray
import com.example.assignment2.ui.theme.LightPink
import com.example.assignment2.ui.theme.MidGray
import com.example.assignment2.ui.theme.MidPink
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White
import com.google.firebase.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun AdminOrderDetailScreen(
    orderId: String,
    viewModel: AdminOrderDetailViewModel = viewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val selectedOrder by viewModel.selectedOrder.collectAsState()
    val orderStatus by viewModel.orderStatus.collectAsState()
    val orderProcessStage by viewModel.orderProcessStages.collectAsState()
    val orderDateTime by viewModel.orderDateTime.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val customerIdFk by viewModel.customerIdFk.collectAsState()


    LaunchedEffect(orderId) {
        viewModel.fetchOrderById(orderId)
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {


        OutlinedTextField(
            value = orderId,
            onValueChange = {},
            label = { Text(text = "Order ID") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = White,
                unfocusedIndicatorColor = DarkGray,
                focusedIndicatorColor = MidPink
            ),
            readOnly = true,
            modifier = Modifier.width(300.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))


        OutlinedTextField(
            value = customerIdFk,
            onValueChange = {},
            label = { Text(text = "Customer ID") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = White,
                unfocusedIndicatorColor = DarkGray,
                focusedIndicatorColor = MidPink
            ),
            readOnly = true,
            modifier = Modifier.width(300.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))


        OutlinedTextField(
            value = orderDateTime,
            onValueChange = {},
            label = { Text(text = "Order Date and Time") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = White,
                unfocusedIndicatorColor = DarkGray,
                focusedIndicatorColor = MidPink
            ),
            readOnly = true,
            modifier = Modifier.width(300.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))


        OutlinedTextField(
            value = totalAmount,
            onValueChange = {},
            label = { Text(text = "Total Amount") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = White,
                focusedContainerColor = White,
                unfocusedIndicatorColor = DarkGray,
                focusedIndicatorColor = MidPink
            ),
            readOnly = true,
            modifier = Modifier.width(300.dp)
        )


        Spacer(modifier = Modifier.height(12.dp))


        // Order Status Dropdown
        ddlOrderStatus(
            orderStatus = orderStatus,
            viewModel = viewModel
        )


        Spacer(modifier = Modifier.height(12.dp))


        // Order Process Stage Dropdown
        ddlOrderProcessStages(
            orderProcessStages = orderProcessStage,
            viewModel = viewModel
        )


        Spacer(modifier = Modifier.height(30.dp))


        Button(
            onClick = {
                Log.d("TAG", "Updating with status: $orderStatus and process: $orderProcessStage")
                viewModel.updateOrder(orderId)
            },
            colors = ButtonDefaults.buttonColors(Orange),
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
        ) {
            Text(text = stringResource(R.string.update))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ddlOrderStatus(
    orderStatus: String,
    viewModel: AdminOrderDetailViewModel
) {
    val list = listOf("Active", "Completed", "Cancelled")
    var selectedText by remember { mutableStateOf(orderStatus) }
    var isExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(orderStatus) {
        selectedText = orderStatus
    }


    Column {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(300.dp),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = White,
                    focusedContainerColor = White,
                    unfocusedIndicatorColor = DarkGray,
                    focusedIndicatorColor = MidPink,
                    unfocusedLabelColor = DarkerGray,
                    focusedLabelColor = MidPink,
                    focusedPlaceholderColor = DarkGray
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                label = {
                    Text(text = "Order Status")
                },
            )


            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .background(LightPink)
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            isExpanded = false
                            viewModel.updateOrderStatus(item)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background( LightPink )
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ddlOrderProcessStages(
    orderProcessStages: String,
    viewModel: AdminOrderDetailViewModel
) {
    val list = listOf("OrderPlaced", "PreppingFlowers", "AwaitingRider", "InDelivery", "Delivered")
    var selectedText by remember { mutableStateOf(orderProcessStages) }
    var isExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(orderProcessStages) {
        selectedText = orderProcessStages
    }


    Column {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .width(300.dp),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = White,
                    focusedContainerColor = White,
                    unfocusedIndicatorColor = DarkGray,
                    focusedIndicatorColor = MidPink,
                    unfocusedLabelColor = DarkerGray,
                    focusedLabelColor = MidPink,
                    focusedPlaceholderColor = DarkGray
                ),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                label = {
                    Text(text = "Order Process Stages")
                },
            )


            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .background(LightPink)
            ) {
                list.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            isExpanded = false
                            viewModel.updateOrderProcessStages(item)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightPink)
                    )
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewAdminOrderDetailScreen() {
    //AdminOrderDetailScreen("000001")
}
