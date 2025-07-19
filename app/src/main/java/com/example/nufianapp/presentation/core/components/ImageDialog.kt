package com.example.nufianapp.presentation.core.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Red
import com.example.nufianapp.ui.theme.White

@Composable
fun ImageDialog(
    imageUri: Uri?,
    takenImageUris: List<Uri>,
    selectedImageIndex: MutableState<Int>,
    dismissDialog: () -> Unit
) {
    imageUri?.let {
        Dialog(
            onDismissRequest = dismissDialog,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Charcoal.copy(alpha = 0.5f))
                    )

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .background(White)
                            .padding(16.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(8.dp),
                                clip = true
                            )
                            .align(Alignment.Center)
                    ) {
                        val painter =
                            rememberAsyncImagePainter(takenImageUris.getOrNull(selectedImageIndex.value))
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape = RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .background(color = Red, shape = CircleShape)
                    ) {
                        IconButton(onClick = dismissDialog) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        itemsIndexed(takenImageUris) { index, uri ->
                            TakenImageItem(modifier = Modifier, uri = uri) {
                                selectedImageIndex.value = index
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TakenImageItem(modifier: Modifier, uri: Uri, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(80.dp)
            .padding(horizontal = 4.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}