package com.example.assignment2.Screen

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.assignment2.ui.theme.DarkerGray
import com.example.assignment2.ui.theme.LightPink
import com.example.assignment2.ui.theme.MidPink
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White
import com.google.firebase.Timestamp
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun OrderManagementScreen(
    navController: NavController,
    viewModel: OrderManagementViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val orders by viewModel.filteredOrderList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 20.dp)
            .fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier
                .padding(8.dp)
                .padding(bottom = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            item {
                Button(
                    onClick = { viewModel.filterOrders(null) }, // Show all orders
                    colors = ButtonDefaults.buttonColors(Orange),
                ) {
                    Text(text = "All")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { viewModel.filterOrders("Active") }, // Show active orders
                    colors = ButtonDefaults.buttonColors(Orange),
                ) {
                    Text(text = "Active")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { viewModel.filterOrders("Completed") }, // Show completed orders
                    colors = ButtonDefaults.buttonColors(Orange),
                ) {
                    Text(text = "Completed")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = { viewModel.filterOrders("Cancelled") }, // Show cancelled orders
                    colors = ButtonDefaults.buttonColors(Orange),
                ) {
                    Text(text = "Cancelled")
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                var colorStatus = AlmostBlack

                if (order.OrderStatus == "Active") {
                    colorStatus = ColorSuccess
                } else if (order.OrderStatus == "Cancelled") {
                    colorStatus = ColorError
                } else if (order.OrderStatus == "Completed") {
                    colorStatus = ColorInfo
                }

                Box(
                    modifier = modifier
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = LightPink
                        )
                        .background(White),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = order.OrderId,
                                    color = DarkerGray,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                )
                                Text(
                                    text = timestampToString(order.OrderDateTime),
                                    color = DarkerGray,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                )
                            }
                            Text(
                                text = order.OrderStatus,
                                color = colorStatus,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.order_price, order.TotalAmount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Black
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = order.CustomerIdFk,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = AlmostBlack
                            )
                            Button(
                                onClick = {  navController.navigate("AdminOrderDetailScreen/${order.OrderId}") },
                                colors = ButtonDefaults.buttonColors(MidPink),
                                modifier = Modifier
                            ) {
                                Text(
                                    text = stringResource(R.string.view_detail).uppercase()
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

//@Composable
//private fun orderCard(
//    orderId: String,
//    orderDateTime: Timestamp,
//    orderStatus: String,
//    totalAmount: String,
//    customerIdFk: String,
//    navController: NavController,
//    order: orders,
//    modifier: Modifier = Modifier
//) {
//
//
//
//}


private fun timestampToString(timestamp: Timestamp?): String {
    return timestamp?.let {
        val milliseconds = it.seconds * 1000 + it.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd MMM yyyy, hh.mm a", Locale.getDefault())
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate)
        Log.d("TAG170", date) // Logging the date for debugging
        date
    } ?: "No Date Available" // Return a default string if timestamp is null
}


