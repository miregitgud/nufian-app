package com.example.nufianapp.presentation.screens.news.add.components

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.data.model.News
import com.example.nufianapp.presentation.screens.home.add.components.ContentImageSelector
import com.example.nufianapp.ui.theme.Blue

@Composable
@ExperimentalComposeUiApi
fun AddNewsContent(
    onNewsDataChanged: (news: News) -> Unit,
    addNews: () -> Unit,
    modifier: Modifier = Modifier,
    takePictureLauncher: (Uri) -> Unit,
    getContentImagesLauncher: ActivityResultLauncher<String>,
    selectedContentImageUris: List<Uri>,
    navigateBack: () -> Unit,
    context: Context
) {
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            IconButton(
                modifier = Modifier.size(48.dp),
                onClick = { navigateBack() }
            ) {
                Icon(
                    tint = Color.White,
                    painter = painterResource(R.drawable.icon_navigation_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .background(color = Blue, shape = CircleShape)
                        .padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Add News", // Title
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload an image and fill in the details",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextFieldWithLabel(
                value = subject,
                onValueChange = { subject = it },
                label = R.string.app_label_subject
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Content TextField
            TextFieldWithLabel(
                value = content,
                onValueChange = { content = it },
                label = R.string.app_label_content,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))


            ContentImageSelector(
                getContentImages = getContentImagesLauncher,
                takePicture = takePictureLauncher,
                selectedContentImageUris = selectedContentImageUris,
                context = context
            )

            selectedImageUris.forEach { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(color = Color.Black.copy(alpha = 0.2f))
                        .padding(16.dp),
                    contentScale = ContentScale.FillBounds
                )
            }

            val news = News(
                subject = subject,
                content = content,
                contentImageUris = selectedContentImageUris,
            )
            onNewsDataChanged(news)

            Spacer(modifier = Modifier.height(48.dp))
            // Add News Button
            ActionButton(
                addNews,
                text = "Add news"
            )
        }
    }
}

@Composable
fun TextFieldWithLabel(
    value: String,
    onValueChange: (String) -> Unit,
    label: Int,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(id = label)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        singleLine = singleLine
    )
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(Blue)
    ) {
        Text(
            text = text,
            fontSize = 15.sp
        )
    }
}