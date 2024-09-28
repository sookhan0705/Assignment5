package com.example.assignment2.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.assignment2.Data.CategoryItems
import com.example.assignment2.Data.ProductItem
import com.example.assignment2.R


@Composable
fun Homepage(viewModel: StoreViewModel,navController: NavController) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()


    // Main Column to hold all components of the homepage
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Search Row
        SearchRow(
            searchQuery = uiState.searchQuery,
            onSearchChange = { query ->
                viewModel.setSearchQuery(query) // Update query in the ViewModel
            },
            onSearchClick = {
                viewModel.filterProductsByQuery() // Trigger filtering when search icon is clicked
            }
        )
        // Banner Section
        Banner(bannerText = uiState.bannerText)


        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }


        // Category List Section
        CategoryList(
            categories = uiState.categories,
            onCategoryClick = viewModel::selectCategory, // Update products on category click
            selectedCategory = uiState.selectedCategory  // Highlight selected category
        )


        Box(
            modifier = Modifier
                .fillMaxWidth(), // Make the Box fill the width of the screen
            contentAlignment = Alignment.Center // Center the content inside the Box
        ) {
            Text(
                text = "Product",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                textAlign = TextAlign.Center // Align text in the center within the Text composable
            )
        }




        // Product List Section (Filtered by selected category)
        ProductList(products = uiState.products, navController =navController )  // Display the filtered products
    }
}


@Composable
fun SearchRow(searchQuery: String, onSearchChange: (String) -> Unit, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange, // Trigger search query change
            label = { Text(text = "Search...", fontStyle = FontStyle.Italic, fontSize = 20.sp) },
            leadingIcon = {
                IconButton(onClick = onSearchClick) { // Trigger search on icon click
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(android.graphics.Color.parseColor("#521c98")), shape = RoundedCornerShape(8.dp))
                .background(Color.White, CircleShape)
        )
    }
}






@Composable
fun Banner(bannerText: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
    ) {
        val (img, text, button) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(img) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )


        Text(
            text = bannerText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .padding(20.dp)
                .constrainAs(text) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )


        Text(
            text = "Buy Now",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .padding(start = 20.dp, top = 15.dp, bottom = 20.dp)
                .constrainAs(button) {
                    top.linkTo(text.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .background(Color.Black, shape = RoundedCornerShape(50.dp))
                .padding(5.dp)
        )
    }
}


@Composable
fun CategoryList(categories: List<CategoryItems>, onCategoryClick: (String) -> Unit, selectedCategory: String) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { item ->
            val isSelected = selectedCategory == item.name
            Column(
                modifier = Modifier
                    .height(180.dp)
                    .width(150.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(10.dp))
                    .background(if (isSelected) Color.Gray else Color.White, shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .clickable { onCategoryClick(item.name) }
            ) {
                ConstraintLayout(modifier = Modifier.height(IntrinsicSize.Max)) {
                    val (topImg, name) = createRefs()


                    Image(
                        painter = painterResource(id = item.picUrl),
                        contentDescription = null,
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .constrainAs(topImg) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            },
                        contentScale = ContentScale.Crop
                    )


                    Text(
                        text = item.name,
                        modifier = Modifier
                            .constrainAs(name) {
                                start.linkTo(parent.start)
                                top.linkTo(topImg.bottom)
                            }
                            .padding(start = 45.dp, top = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ProductList(products: List<ProductItem>,navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .width(180.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(10.dp))
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(FlowerScreen.Product.name)
                    }
            ) {
                ConstraintLayout(modifier = Modifier.height(IntrinsicSize.Max)) {
                    val (topImg, name, price) = createRefs()


                    // Display product image
                    Image(
                        painter = rememberAsyncImagePainter(model = product.photo), // Product image URL from Firestore
                        contentDescription = null,
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .constrainAs(topImg) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            },
                        contentScale = ContentScale.Crop
                    )


                    // Display product name
                    Text(
                        text = product.productName,
                        modifier = Modifier
                            .constrainAs(name) {
                                start.linkTo(parent.start)
                                top.linkTo(topImg.bottom)
                            }
                            .padding(start = 50.dp, top = 5.dp)
                    )


                    // Display product price
                    Text(
                        text = "RM${product.productPrice}",
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        modifier = Modifier
                            .constrainAs(price) {
                                start.linkTo(parent.start)
                                top.linkTo(name.bottom)
                            }
                            .padding(start = 50.dp, top = 5.dp)
                    )
                }
            }
        }
    }
}


