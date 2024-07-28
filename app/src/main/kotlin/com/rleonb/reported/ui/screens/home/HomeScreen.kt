package com.rleonb.reported.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rleonb.reported.R
import com.rleonb.reported.ui.designsystem.components.loading.LoadingScreen


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState) {
        HomeUiState.Loading -> LoadingScreen()

        is HomeUiState.Success -> {
            val newsList = (uiState as HomeUiState.Success).newsList

            fun uriToBitmap(selectedFileUri: Uri): Bitmap {
                val parcelFileDescriptor =
                    context.contentResolver.openFileDescriptor(selectedFileUri, "r")
                val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                return image
            }

            var imageUri: Uri? = Uri.EMPTY

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
//                        var exif: ExifInterface? = null
//                        try {
//                            exif = ExifInterface(imageUri!!.path!!)
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                        val orientation: Int = exif!!.getAttributeInt(
//                            ExifInterface.TAG_ORIENTATION,
//                            ExifInterface.ORIENTATION_UNDEFINED
//                        )
//
                        val inputImage = imageUri?.let { uri -> uriToBitmap(uri) }
//                        val bmRotated = inputImage?.let { rotateBitmap(it, orientation) }
                        if (inputImage != null) {
                            viewModel.createNews(inputImage)
                        }
                    }
                }
            )

            fun openCamera() {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.TITLE, "New Picture")
                    put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                }
                imageUri =
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                if (imageUri != null) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    cameraLauncher.launch(cameraIntent)
                }
            }

            val permission = Manifest.permission.CAMERA
            val permissionState = rememberPermissionState(permission)
            var text by remember { mutableStateOf("") } // Query for SearchBar

            DisposableEffect(Unit) {
                if (!permissionState.status.isGranted) {
                    permissionState.launchPermissionRequest()
                }
                onDispose { }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SearchTopBar(
                        text = text,
                        onValueChange = { newText -> text = newText },
                        onSearchClicked = { query -> viewModel.searchNews(query) },
                        onCloseClicked = { text = "" }
                    )

                    LazyColumn {
                        items(newsList) { news ->
                            var sizeImage by remember { mutableStateOf(IntSize.Zero) }
                            val gradient = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = sizeImage.height.toFloat() / 3,  // 1/3
                                endY = sizeImage.height.toFloat()
                            )

                            Card(
                                modifier = Modifier
                                    .shadow(4.dp)
                                    .padding(bottom = 8.dp)
                            ) {
                                Box {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(news!!.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .onGloballyPositioned { coordinates ->
                                                sizeImage = coordinates.size
                                            }
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(gradient)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = news.title,
                                            color = Color.White
                                        )
                                        Text(
                                            text = news.address,
                                            color = Color.White
                                        )
                                        Text(
                                            text = news.createdAt,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Button for opening camera
                ExtendedFloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = {
                        if (permissionState.status.isGranted) {
                            openCamera()
                        } else {
                            permissionState.launchPermissionRequest()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Camera, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.photo_button))
                }
            }
        }

        is HomeUiState.Error -> {
            throw (uiState as HomeUiState.Error).exception
        }
    }
}

@Composable
fun SearchTopBar(
    text: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onCloseClicked: () -> Unit
) {
    SearchWidget(
        text = text,
        onValueChange = onValueChange,
        onSearchClicked = onSearchClicked, onCloseClicked = onCloseClicked
    )
}

@Composable
fun SearchWidget(
    text: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onCloseClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Search for problems or area",
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {
                        onSearchClicked(text)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onValueChange("")
                        } else {
                            onCloseClicked()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "close",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
        )
    }
}

fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_NORMAL -> return bitmap
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
            matrix.setRotate(180f)
            matrix.postScale(-1f, 1f)
        }

        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.setRotate(90f)
            matrix.postScale(-1f, 1f)
        }

        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.setRotate(-90f)
            matrix.postScale(-1f, 1f)
        }

        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
        else -> return bitmap
    }
    try {
        val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return bmRotated
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        return null
    }
}