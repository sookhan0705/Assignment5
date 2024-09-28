package com.example.assignment2.Screen

import CartScreen
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment2.Data.OrderRepo
import com.example.assignment2.Data.ProductRepo
import com.example.assignment2.R
import com.google.firebase.firestore.FirebaseFirestore


// Enum class for different screens in the app
enum class FlowerScreen(@StringRes val title: Int) {
    Welcome(R.string.welcome),
    SignUp(R.string.sign_up),
    Login(R.string.login),
    ResetPassword(R.string.reset_password),
    FlowerHome(R.string.flower_home),
    Profile(R.string.user_profile),
    AddProduct(R.string.add_product),
    ProductInventory(R.string.product_inventory),
    Cart(title = R.string.cart),
    PaymentMethod(title = R.string.payment_Method),
    CreditCard(title = R.string.credit_card),
    PaymentSuccess(title = R.string.payment_success),
    OrderHistory(title = R.string.my_order){},
    TrackOrder(title = R.string.track_order),
    AdminOrderManagement(title = R.string.order_management),
    AdminOrderDetail(title = R.string.order_detail),
    Product(title = R.string.product)


}


// Composable function to set up the navigation and top bar
@Composable
fun FlowerApp(
    viewModel: StoreViewModel = viewModel(),
    productRepo: ProductRepo,
    pyViewModel: PaymentViewModel = viewModel(),
    orderRepo: OrderRepo,
//    orderHistoryViewModel: FirestoreOrderHistoryViewModel = viewModel(),
//    orderManagementViewModel: OrderManagementViewModel = viewModel(),
//    adminOrderDetailViewModel: AdminOrderDetailViewModel = viewModel()

) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = when {
        currentRoute == null -> FlowerScreen.Welcome
        currentRoute.startsWith("ProductDetailScreen/") -> FlowerScreen.ProductInventory
        else -> try {
            FlowerScreen.valueOf(currentRoute)
        } catch (e: IllegalArgumentException) {
            FlowerScreen.Welcome
        }
    }


    // List of screens that do not need the bottom bar
    val noBottomBarScreens = listOf(FlowerScreen.Welcome.name,FlowerScreen.SignUp.name, FlowerScreen.Login.name, FlowerScreen.ResetPassword.name)


    Scaffold(
        topBar = {
            FlowerAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            // Only show the bottom bar if the current screen is not in the "noBottomBarScreens" list
            if (currentScreen.name !in noBottomBarScreens) {
                AppBottomBar(navController = navController)  // Add BottomAppBar here for certain screens
            }
        }


    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FlowerScreen.Welcome.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(FlowerScreen.Welcome.name) {
                WelcomeScreen(navController = navController)
            }
            composable(FlowerScreen.SignUp.name) {
                SignUpScreen(navController = navController, viewModel = viewModel)
            }
            composable(FlowerScreen.Login.name) {
                LoginScreen(navController = navController, viewModel = viewModel)
            }
            composable(FlowerScreen.ResetPassword.name) {
                ResetPasswordScreen(navController = navController, viewModel = viewModel)
            }
            composable(FlowerScreen.FlowerHome.name) {
                Homepage(viewModel = viewModel,navController)
            }
            composable("search") {
                Text("Search Screen")  // Replace with actual search screen
            }
            composable("cart") {
                Text("Cart Screen")  // Replace with actual cart screen
            }
            composable(FlowerScreen.Profile.name) {
                UserProfileScreen(navController = navController,viewModel = viewModel)
            }
            composable(FlowerScreen.ProductInventory.name) {
                ProductInventoryScreen(navController = navController, repository = productRepo)
            }
            composable("ProductDetailScreen/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                if (productId != null) {
                    ProductDetailScreen(productId = productId)
                }
                else {
                    // Handle the null case or log it for debugging
                    Log.e("Navigation", "Product ID is null or invalid")
                }
            }
            composable(FlowerScreen.AddProduct.name) {
                AddProductScreen()
            }

            composable(route = FlowerScreen.Product.name) {
                ProductScreen(
                    context = LocalContext.current,
                    db = FirebaseFirestore.getInstance(),  // Pass Firestore instance to ProductScreen
                    onAddToCart = { product ->
                    },
                    onNextButtonClicked = {
                        navController.navigate(FlowerScreen.Cart.name)
                    }
                )
            }

            composable(route = FlowerScreen.Cart.name) {
                CartScreen(
                    userViewModel = viewModel,
                    context = LocalContext.current,
                    db = FirebaseFirestore.getInstance(),  // Pass Firestore instance to CartScreen
                    //userId = "USER_ID",
                    onCheckoutClicked = {
                        navController.navigate(FlowerScreen.PaymentMethod.name)
                    }
                )
            }



            composable(route = FlowerScreen.PaymentMethod.name) {
                PaymentMethodScreen(
                    db = FirebaseFirestore.getInstance(),
                    onNextButtonClicked = { selectedPaymentMethod ->
                        pyViewModel.setPaymentMethod(selectedPaymentMethod)
                        if (selectedPaymentMethod == "Credit Card") {
                            navController.navigate(FlowerScreen.CreditCard.name)
                        } else {
                            navController.navigate(FlowerScreen.PaymentSuccess.name)
                        }
                    },
                    onCancelButtonClicked = {
                        cancelPaymentAndNavigateToStart(pyViewModel, navController)
                    },
                    navigateToCart = {
                        navController.navigate(FlowerScreen.Cart.name)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }


            composable(route = FlowerScreen.CreditCard.name) {
                CreditCardScreen(
                    db = FirebaseFirestore.getInstance(),
                    onNextButtonClicked = {
                        navController.navigate(FlowerScreen.PaymentSuccess.name)
                    },
                    modifier = Modifier.fillMaxSize() // Make sure to use fillMaxSize here
                )
            }


            composable(route = FlowerScreen.PaymentSuccess.name) {
                PaymentSuccessfulScreen(
                    onDoneButtonClicked = {
                        navController.popBackStack(FlowerScreen.PaymentMethod.name, inclusive = false)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(FlowerScreen.TrackOrder.name) {
                TrackOrderScreen(navController = navController)
            }


            composable(FlowerScreen.OrderHistory.name) {
                OrderHistoryScreen(
                    navController = navController,
                    repository = orderRepo,
                    userViewModel = viewModel,
                    onTOButtonClicked = { navController.navigate(FlowerScreen.TrackOrder.name) },
                )
            }

        }
    }
}



private fun cancelPaymentAndNavigateToStart(
    viewModel: PaymentViewModel,
    navController: NavHostController
) {
    viewModel.resetPayment()
    navController.popBackStack(FlowerScreen.PaymentMethod.name, inclusive = false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerAppBar(
    currentScreen: FlowerScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            } else null
        } // Return null if there's no back navigation
    )
}




@Composable
fun AppBottomBar(
    navController: NavHostController,  // Pass NavController to handle navigation
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = Color(0xFFFFC1E3),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = { navController.navigate(FlowerScreen.FlowerHome.name) },  // Navigate to Home
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(text = "Home")
                }
            }


            IconButton(
                onClick = { navController.navigate("search") },  // Navigate to Search
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(text = "Search")
                }
            }


            IconButton(
                onClick = { navController.navigate(FlowerScreen.Cart.name) },  // Navigate to Cart
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.ShoppingCart, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(text = "Cart")
                }
            }

            IconButton(
                onClick = { navController.navigate(FlowerScreen.OrderHistory.name) },  // Navigate to Cart
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(text = "History")
                }
            }


            IconButton(
                onClick = { navController.navigate("profile") },  // Navigate to ProfileScreen
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Rounded.Person, contentDescription = null, modifier = Modifier.size(24.dp))
                    Text(text = "Profile")
                }
            }
        }
    }
}
