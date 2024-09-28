package com.example.assignment2.Screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.assignment2.Data.ProductRepo
import com.example.assignment2.Data.ProductViewModelFactory
import com.example.assignment2.R
import com.example.assignment2.ui.theme.Assignment2Theme
import com.example.assignment2.ui.theme.BlushPink
import com.example.assignment2.ui.theme.DarkGray
import com.example.assignment2.ui.theme.DarkerGray
import com.example.assignment2.ui.theme.LightGray
import com.example.assignment2.ui.theme.Orange
import com.example.assignment2.ui.theme.White
import com.example.assignment2.ui.theme.lightBeige
import com.example.assignment2.ui.theme.softWhite
import java.util.concurrent.Flow

@Composable

fun ProductInventoryScreen(repository:ProductRepo, modifier: Modifier = Modifier,navController: NavController) {
    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(repository)
    )

    val productCount by viewModel.productCount.collectAsState()
    val totalStock by viewModel.totalStock.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProduct()
        viewModel.countTotalProducts()

    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = White)) {
        Box(modifier = Modifier
            .padding(top = 8.dp)
//            .background(color = colorResource(id = R.color.infoboxBackground), shape = RoundedCornerShape(20.dp))
            ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                infoBox(num = "$productCount" ,tittle = "Total Product", modifier =
                Modifier
                    .height(100.dp)
                    .width(150.dp))
                infoBox(num = "$totalStock", tittle = "TotalStock", modifier = Modifier
                    .height(100.dp)
                    .width(150.dp))
            }
        }

        // Move the button outside the LazyColumn and position it at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth(), // Add some padding for better spacing
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate(FlowerScreen.AddProduct.name)},
                colors = ButtonDefaults.buttonColors(Orange),
                shape = RoundedCornerShape(8.dp)
            ,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(text = "Add Product")
            }
        }

        val productList by viewModel.products.collectAsStateWithLifecycle()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(productList) { product ->
                Box(
                    modifier = modifier
                        .clip(RoundedCornerShape(10.dp))
                        .padding(8.dp)
                        .border(2.dp, DarkerGray, RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("ProductDetailScreen/${product.productId}")
                        }
                ) {
                    Row {
                        // Set a fixed size for the Box containing the image
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(
                                model = product.photo,
                                contentDescription = "Flower",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = product.productName, textAlign = TextAlign.Center)
                            Text(text = "RM "+product.productPrice, textAlign = TextAlign.Center)
                            Canvas(modifier = Modifier.fillMaxWidth()) {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(x = 0f, y = size.height / 2),
                                    end = Offset(x = size.width, y = size.height / 2),
                                    strokeWidth = 5f
                                )
                            }
                            Row(modifier = Modifier.padding(start = 40.dp)) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = product.productQuantity + "Unit",
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                    Text(text = "In Stock")
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = product.LastRestock + "Unit")
                                    Text(text = "Last Restock", textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}




@Composable
fun infoBox(num:String, tittle:String, modifier: Modifier=Modifier){
    Box(modifier = modifier
        .background(
            color = BlushPink,
            shape = RoundedCornerShape(16.dp)
        )
        .size(80.dp),
        contentAlignment = Alignment.Center
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text =num,modifier=Modifier)
            Text(text = tittle )
        }
    }
}


