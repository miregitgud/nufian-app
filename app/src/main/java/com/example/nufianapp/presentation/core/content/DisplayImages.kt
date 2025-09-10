package com.example.nufianapp.presentation.core.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.presentation.core.components.ImageContentUrl

@Composable
fun DisplayImages(imageUrls: List<String>, modifier: Modifier = Modifier) {
    var enlargedImageUrl by remember { mutableStateOf<String?>(null) }
    var scale by remember { mutableStateOf(1f) }

    // Function to show enlarged image
    fun showEnlargedDialog(url: String) {
        enlargedImageUrl = url
    }

    // Function to dismiss enlarged image overlay
    fun dismissEnlargedDialog() {
        enlargedImageUrl = null
        scale = 1f // Reset the scale when dismissing the dialog
    }

    // Main content
    Column(modifier = modifier.fillMaxSize()) {
        if (imageUrls.size == 1) {
            ImageContentUrl(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .height(200.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            showEnlargedDialog(imageUrls[0])
                        }
                    },
                contentUrl = imageUrls[0],

            )
        } else if (imageUrls.size > 1) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUrls) { imageUrl ->
                    ImageContentUrl(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .pointerInput(Unit) {
                                 detectTapGestures {
                                    showEnlargedDialog(imageUrl)
                                }
                            },
                        contentUrl = imageUrl
                    )
                }
            }
        }

        // Overlay to show enlarged image using dialog
        if (enlargedImageUrl != null) {
            EnlargedImageDialog(
                imageUrl = enlargedImageUrl!!,
                scale = scale,
                setScale = { newScale -> scale = newScale.coerceIn(1f, 5f) },
                onDismiss = { dismissEnlargedDialog() }
            )
        }
    }
}

@Composable
fun EnlargedImageDialog(imageUrl: String, scale: Float, setScale: (Float) -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        setScale(scale * zoom)
                    }
                }
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun NewsItem(newsTitle: String, newsDescription: String, onNewsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onNewsClick() } // Navigate to DetailNews
            .padding(16.dp)
    ) {
        Text(text = newsTitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = newsDescription, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DisplayNews(newsList: List<NewsItemData>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(newsList) { newsItem ->
            NewsItem(
                newsTitle = newsItem.title,
                newsDescription = newsItem.description,
                onNewsClick = { /* Navigate to DetailNews */ }
            )
        }
    }
}

data class NewsItemData(val title: String, val description: String)