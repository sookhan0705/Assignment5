package com.example.assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.assignment2.Data.AppDatabase
import com.example.assignment2.Data.CreditCardDatabase
import com.example.assignment2.Data.CreditCardRepository
import com.example.assignment2.Data.OrderDatabase
import com.example.assignment2.Data.OrderRepo
import com.example.assignment2.Data.ProductRepo
import com.example.assignment2.Data.UserDatabase
import com.example.assignment2.Data.UserFactory
import com.example.assignment2.Data.UserRepository
import com.example.assignment2.Screen.AddProductScreen
import com.example.assignment2.Screen.CreditCardViewModel
import com.example.assignment2.Screen.FlowerApp
import com.example.assignment2.Screen.ProductDetailScreen
import com.example.assignment2.Screen.ProductInventoryScreen
import com.example.assignment2.Screen.StoreViewModel
import com.example.assignment2.ui.theme.Assignment2Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            CreditCardDatabase::class.java,
            "CC.db"
        ).build()
    }
    private val ccviewModel by viewModels<CreditCardViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CreditCardViewModel(CreditCardRepository(database)) as T
                }
            }
        }
    )

    // Initialize Room Database lazily
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java,
            "user.db"
        ).build()
    }

    private lateinit var productRepository: ProductRepo
    private lateinit var orderRepo: OrderRepo



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
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
                    val viewModel: StoreViewModel = viewModel(
                        factory = UserFactory(
                            context = applicationContext,
                            database = db
                        )
                    )

                    FlowerApp(productRepo = productRepository,orderRepo = orderRepo, viewModel = viewModel, CCviewModel = ccviewModel)
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

