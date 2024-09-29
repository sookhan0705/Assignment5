package com.example.assignment2.Screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.assignment2.R
import com.example.assignment2.ui.theme.Assignment2Theme
import com.example.assignment2.ui.theme.Black
import com.example.assignment2.ui.theme.LightPink
import com.example.assignment2.ui.theme.Orange

@Composable
fun AddProductScreen(addProductViewModel: AddProductViewModel = viewModel()){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(enabled = true, state = scrollState)
        , horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = addProductViewModel.productName,
            onValueChange = {addProductViewModel.productName = it},
            label = { Text(text = "ProductName") },
            modifier = Modifier.padding(8.dp),
            isError = addProductViewModel.productNameError  !=null
        )

        if (addProductViewModel.productNameError != null) {
            Text(
                text = addProductViewModel.productNameError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
        dropDownlist(addProductViewModel)
        OutlinedTextField(
            value = addProductViewModel.productPrice,
            onValueChange = {addProductViewModel.productPrice = it},
            label = { Text(text = "ProductPrice") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(8.dp),
            isError = addProductViewModel.productPriceError != null

        )
            if (addProductViewModel.productPriceError != null) {
            Text(
                text = addProductViewModel.productPriceError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
            }

        OutlinedTextField(
            value = addProductViewModel.productQuantity,
            onValueChange = { addProductViewModel.productQuantity = it },
            label = { Text(text = "Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(8.dp),
            isError = addProductViewModel.productQuantityError != null
        )
        if (addProductViewModel.productQuantityError != null) {
            Text(
                text = addProductViewModel.productQuantityError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            addProductViewModel.productPhoto = uri // Update photo URI in ViewModel
        }
        // Display the selected image
        addProductViewModel.productPhoto?.let { imageUri ->
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(200.dp) // Adjust size as needed
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { launcher.launch("image/*") }, colors = ButtonDefaults.buttonColors(Orange)) {
            Text("Select Image")


        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { addProductViewModel.createProduct(context) },colors = ButtonDefaults.buttonColors(Orange)) {
            Text(text = "AddProduct")
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropDownlist(viewModel: AddProductViewModel) {
    // Define your list of items
    val list = listOf("Flower", "Basket", "Bouquet")

    // Local state to manage the selected value
    var selectedText by remember {
        mutableStateOf("")
    }

    // Local state to manage dropdown expansion
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Column() {
        // Exposed dropdown menu box
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            // Outlined text field for dropdown (readonly)
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                label = { Text(text = "Select Category") } // Label for the dropdown
            )

            // The actual dropdown menu
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                list.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category) },
                        onClick = {
                            selectedText = category  // Update selectedText state
                            isExpanded = false  // Close dropdown after selection

                            // Update the ViewModel with the selected category
                            viewModel.updateProductCategory(category)
                        }
                    )
                }
            }
        }
        if (viewModel.productCategoryError != null) {
            Text(
                text = viewModel.productCategoryError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBar(name:String,canNavigate:Boolean,navigateUp:()->Unit,modifier: Modifier=Modifier){
    Row(modifier = modifier) {
        TopAppBar(title = {
            Box(modifier = Modifier.fillMaxWidth()
                , contentAlignment = Alignment.Center){
                Text(text = name, fontSize = 20.sp)
            }
        },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPink),
            modifier = modifier,
            navigationIcon = {
                if (canNavigate) {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            }

        )
    }

}



@Preview(showBackground = true)
@Composable
fun AddproductPreview(){
    Assignment2Theme {
        AddProductScreen()
    }
}


