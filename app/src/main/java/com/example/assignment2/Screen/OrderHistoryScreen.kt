package com.example.assignment2.Screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.assignment2.Data.OrderHistoryFactory
import com.example.assignment2.Data.OrderRepo
import com.example.assignment2.R
import com.example.assignment2.ui.theme.AlmostBlack
import com.example.assignment2.ui.theme.Black
import com.example.assignment2.ui.theme.ColorError
import com.example.assignment2.ui.theme.ColorInfo
import com.example.assignment2.ui.theme.ColorSuccess
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
fun OrderHistoryScreen(
//    userId: String,
    repository : OrderRepo,
    navController: NavController,
    modifier: Modifier = Modifier,
    userViewModel :StoreViewModel,
    onTOButtonClicked: () -> Unit = { }
) {
    val viewModel :FirestoreOrderHistoryViewModel = viewModel(
        factory = OrderHistoryFactory(repository)
    )
    val orders by viewModel.filteredOrderList.collectAsStateWithLifecycle()
    val userId = userViewModel.getUserId()

    val filteredOrders = orders
        .filter { it.CustomerIdFk == userId }
        .sortedByDescending { it.OrderDateTime.seconds }

    LaunchedEffect(Unit) {
        viewModel.filterOrders(null)
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier
                .padding(8.dp)
                .padding(top = 12.dp, bottom = 12.dp)
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


        Box(
            modifier = modifier
                .fillMaxSize()
                .weight(1.0f)
        ) {
            Column {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f) // This makes the LazyColumn take available space
                ) {
                    items(filteredOrders) { order ->
                        OrderProductCards(
                            orderId = order.OrderId,
                            orderDateTime = order.OrderDateTime,
                            orderStatus = order.OrderStatus,
                            totalAmount = order.TotalAmount
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(
            onClick = onTOButtonClicked,
            colors = ButtonDefaults.buttonColors(Orange),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
        ) {
            Text(text = stringResource(R.string.track_order))
        }
        Spacer(modifier = Modifier.height(36.dp))
    }

}


@Composable
fun OrderProductCards(
    orderId: String,
    orderDateTime: Timestamp,
    orderStatus: String,
    totalAmount: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    var colorStatus = AlmostBlack


    if (orderStatus == "Active") {
        colorStatus = ColorSuccess
    } else if (orderStatus == "Cancelled") {
        colorStatus = ColorError
    } else if (orderStatus == "Completed") {
        colorStatus = ColorInfo
    }


    Card (
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        border = BorderStroke(1.dp, LightPink),
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ){
            Text(
                text = orderStatus,
                color = colorStatus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column () {
                    Text(
                        text = stringResource(id = R.string.order_id).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = DarkerGray,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orderId,
                        fontWeight = FontWeight.SemiBold,
                        color = Black,
                    )
                }
                Button(
                    onClick = { copyToClipboard(context, orderId) },
                    colors = ButtonDefaults.buttonColors(White),
                ) {
                    Text(
                        text = stringResource(id = R.string.order_copy_id),
                        fontWeight = FontWeight.Bold,
                        color = MidPink,
                    )
                }
            }
            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MidGray,
            )


            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = timestampToString(orderDateTime)
                )
                Text(
                    text = stringResource(R.string.order_price, totalAmount),
                    fontWeight = FontWeight.SemiBold,
                    color = Black,
                )
            }


        }
    }
    Spacer(modifier = Modifier.height(28.dp))
}


private fun copyToClipboard(context: Context, orderId: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Order ID", orderId)
    clipboardManager.setPrimaryClip(clip)


    Toast.makeText(context, "Order ID copied to clipboard", Toast.LENGTH_SHORT).show()
}


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
