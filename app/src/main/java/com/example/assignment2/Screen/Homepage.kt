package com.example.assignment2.Screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.assignment2.Data.CategoryItems
import com.example.assignment2.Data.ProductItem
import com.example.assignment2.R

@Composable
fun Homepage(navController: NavHostController, viewModel: StoreViewModel) {
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
        Banner(bannerText = uiState.bannerText,navController)

        // Category Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB2B2B2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // Category List Section
        CategoryList(
            categories = uiState.categories,
            onCategoryClick = viewModel::selectCategory, // Update products on category click
            selectedCategory = uiState.selectedCategory  // Highlight selected category
        )

        // Product Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB2B2B2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Product",
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // Product List Section (Filtered by selected category)
        ProductList(products = uiState.products,navController)  // Display the filtered products

        // "See All" Button
        TextButton(onClick = { navController.navigate(FlowerScreen.Product.name) },
            modifier = Modifier
                .fillMaxWidth()) {
            Text(
                text = "See All",
                modifier = Modifier.fillMaxWidth(),  // Ensures that the text takes up the full width
                textAlign = TextAlign.Center,
                color = Color.Blue,
            )
        }
    }
}

@Composable
fun SearchRow(searchQuery: String, onSearchChange: (String) -> Unit, onSearchClick: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = {
                if (!hasFocus && searchQuery.isEmpty()) {
                    Text(text = "Search...", fontStyle = FontStyle.Italic, fontSize = 20.sp)
                }
            },
            leadingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    hasFocus = focusState.isFocused
                }
                .border(1.dp, Color(android.graphics.Color.parseColor("#521c98")), shape = RoundedCornerShape(8.dp))
                .background(Color.White),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
        )
    }
}

@Composable
fun Banner(bannerText: String,navController: NavHostController) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
    ) {
        val (img, text, button) = createRefs()

        // Banner image
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

        // Banner text
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

        // Buy now button
        Text(
            text = "Buy Now",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .padding(start = 20.dp, top = 15.dp, bottom = 20.dp)
                .clickable { navController.navigate(FlowerScreen.Product.name) }
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
                    .background(
                        if (isSelected) Color(0xFFFFC1E3)
                        else Color(0xFFFDEDED),
                        shape = RoundedCornerShape(10.dp))
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
                                end.linkTo(parent.end) // Center the image horizontally
                            },
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .constrainAs(name) {
                                top.linkTo(topImg.bottom)
                                centerHorizontallyTo(parent) // Center the text horizontally
                            }
                            .padding(top = 3.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


@Composable
fun ProductList(products: List<ProductItem>,navController: NavHostController) {
    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .width(150.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(10.dp))
                    .background(Color(0xFFFDEDED), shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .clickable {  navController.navigate(FlowerScreen.Product.name) }
            ) {
                ConstraintLayout(modifier = Modifier.height(IntrinsicSize.Max)) {
                    val (topImg, name, price) = createRefs()

                    // Product image
                    Image(
                        painter = rememberAsyncImagePainter(model = product.photo),
                        contentDescription = null,
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .constrainAs(topImg) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        contentScale = ContentScale.Crop
                    )

                    // Product name
                    Text(
                        text = product.productName,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .constrainAs(name) {
                                top.linkTo(topImg.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .padding(top = 3.dp),
                        fontSize = 17.sp
                    )

                    // Product price
                    Text(
                        text = "RM${product.productPrice}",
                        color = Color.Black,
                        modifier = Modifier
                            .constrainAs(price) {
                                top.linkTo(name.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

