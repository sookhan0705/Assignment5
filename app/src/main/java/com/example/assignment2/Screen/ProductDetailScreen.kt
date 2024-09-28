package com.example.assignment2.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.assignment2.ui.theme.Assignment2Theme

@Composable
fun ProductDetailScreen(productId: String,viewModel: ProductDetailViewModel= viewModel()) {
  val context = LocalContext.current
  LaunchedEffect(productId) {
    viewModel.getProductById(productId)
  }

  val selectedProduct by viewModel.selectedProduct.collectAsState()
  val productName by viewModel.productName.collectAsState()
  val productCategory by viewModel.productCategory.collectAsState()
  val productPrice by viewModel.productPrice.collectAsState()
  val productQuantity by viewModel.productQuantity.collectAsState()
  val restockQuantity by viewModel.restockQuantity.collectAsState()

Column(modifier = Modifier){
  appBar(name = "Product Detail", canNavigate = true , navigateUp = { /*TODO*/ })
  if (selectedProduct == null) {
    Text(text = "Loading...")
  } else {
    selectedProduct?.let { product ->
      Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp) ,verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

        AsyncImage(model = product.photo, contentDescription = "SelectedproductPhoto" , modifier = Modifier.size(200.dp))
        OutlinedTextField(
          value = productName,
          onValueChange = {viewModel.setProductName(it)}
          , label = { Text(text = "ProductName") }
        )
        OutlinedTextField(value = productCategory,
          onValueChange = {},
          label = { Text(text = "ProductCategory") },
          readOnly = true )
        OutlinedTextField(value = productPrice,
          onValueChange = {viewModel.setProductPrice(it)}
          , label = { Text(text = "ProductPrice") })
        OutlinedTextField(value = productQuantity, onValueChange = {}
          ,readOnly = true, label = { Text(text = "ProductQuantity") })
        OutlinedTextField(value = restockQuantity,
          onValueChange = {viewModel.setRestockQuantity(it)}, label = { Text(text = "RestockQuantity") })
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { viewModel.updateProduct(productId,context) }) {
          Text(text = "Update Product")
        }

      }
    } ?: Text(text = "Product not found")
  }
}

}


@Composable
fun ProductDetailPreview(){


}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview2() {
  Assignment2Theme {
    ProductDetailPreview()
  }
}