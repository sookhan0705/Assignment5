package com.example.assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.assignment2.Data.AppDatabase
import com.example.assignment2.Data.OrderDatabase
import com.example.assignment2.Data.OrderRepo
import com.example.assignment2.Data.ProductRepo
import com.example.assignment2.Screen.AddProductScreen
import com.example.assignment2.Screen.FlowerApp
import com.example.assignment2.Screen.ProductDetailScreen
import com.example.assignment2.Screen.ProductInventoryScreen
import com.example.assignment2.ui.theme.Assignment2Theme

class MainActivity : ComponentActivity() {
    private lateinit var productRepository: ProductRepo
    private lateinit var orderRepo: OrderRepo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productDao = AppDatabase.getDatabase(this).productDao()
        productRepository = ProductRepo(productDao)
        val orderDao = OrderDatabase.getDatabase(this).orderDao()
        orderRepo = OrderRepo(orderDao)
        enableEdgeToEdge()
        setContent {
            Assignment2Theme {


                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    //OrderHistoryScreen(haveOrder = true)
                    //AppScreenWithBottomBar()
//                    ProductInventoryScreen(modifier = Modifier, navController = rememberNavController())
//                   AddProductScreen()
//                    AppNavHost(productRepository)
                    FlowerApp(productRepo = productRepository,orderRepo = orderRepo)
                }

            }

        }
    }
}


@Composable
fun AppNavHost(productRepo: ProductRepo) {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "ProductInventoryScreen") {
        composable("ProductInventoryScreen") {
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
        composable("AddProductScreen") {
           AddProductScreen()
        }
    }
}

