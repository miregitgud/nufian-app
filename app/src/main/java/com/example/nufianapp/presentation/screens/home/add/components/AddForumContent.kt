package com.example.nufianapp.presentation.screens.home.add.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.presentation.core.components.ImageDialog
import com.example.nufianapp.presentation.screens.home.add.viewmodel.AddForumViewModel
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun AddForumContent(
    modifier: Modifier = Modifier,
    onForumDataChanged: (forum: Forum) -> Unit,
    categories: List<ForumCategory>,
    selectedForumCategory: ForumCategory?,
    selectedContentImageUris: List<Uri>,
    takePictureLauncher: (Uri) -> Unit,
    getContentImagesLauncher: ActivityResultLauncher<String>,
    context: Context,
    viewModel: AddForumViewModel
) {
    var subjectState by remember { mutableStateOf("") }
    var contentState by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = modifier.height(16.dp))

        AddItemCategorySelection(
            categories = categories,
            selectedForumCategory = selectedForumCategory,
            onCategorySelected = { selectedCategory ->
                viewModel.selectForumCategory(selectedCategory)
            },
            userType = viewModel.userType // <-- Pass the userType here
        )

        Column(modifier = modifier.padding(16.dp)) {

            AddItemSubjectRow(
                avatarUrl = viewModel.avatarUrl,
                userType = viewModel.userType,
                subjectState = subjectState
            ) { subject ->
                subjectState = subject
            }

            Spacer(modifier = modifier.height(16.dp))

            AddItemContentInput(
                contentState = contentState,
                onContentChange = { content ->
                    contentState = content
                }
            )

            Spacer(modifier = modifier.height(16.dp))

            ContentImageSelector(
                getContentImages = getContentImagesLauncher,
                takePicture = takePictureLauncher,
                selectedContentImageUris = selectedContentImageUris,
                context = context
            )

            val forum = Forum(
                forumUserPostId = viewModel.userId,
                subject = subjectState,
                content = contentState,
                topic = selectedForumCategory?.name ?: "General",
                contentImageUris = selectedContentImageUris,
                likes = 0
            )
            onForumDataChanged(forum)
        }
    }
}

@Composable
fun ContentImageSelector(
    modifier: Modifier = Modifier,
    getContentImages: ActivityResultLauncher<String>,
    takePicture: (Uri) -> Unit,
    selectedContentImageUris: List<Uri>,
    context: Context
) {
    val isCameraPermissionGranted by rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        context = context
    )

    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            createImageUri(context)?.let { takePicture(it) }
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to take pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val dialogState = remember { mutableStateOf<Uri?>(null) }
    val dismissDialog = { dialogState.value = null }

    val maxImages = 5

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Add buttons row first
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AddItemImageSelector(
                modifier = Modifier
                    .height(100.dp),
                onClick = {
                    if (selectedContentImageUris.size < maxImages) {
                        getContentImages.launch("image/*")
                    } else {
                        Toast.makeText(
                            context,
                            "You can't add more than 5 images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                icon = R.drawable.icon_item_gallery
            )

            AddItemImageSelector(
                modifier = Modifier
                    .height(100.dp),
                onClick = {
                    if (selectedContentImageUris.size < maxImages) {
                        if (isCameraPermissionGranted) {
                            createImageUri(context)?.let { uri -> takePicture(uri) }
                        } else {
                            requestCameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "You can't add more than 5 images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                icon = R.drawable.icon_item_camera
            )
        }

        // If there are selected images, create a section for them with a title
        if (selectedContentImageUris.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Selected Images (${selectedContentImageUris.size}/$maxImages)",
                style = MaterialTheme.typography.labelLarge,
                color = Graphite
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Display images in a grid-like pattern - single row with horizontal scroll would be an alternative
            selectedContentImageUris.chunked(3).forEach { rowImages ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowImages.forEach { uri ->
                        ContentImage(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp), // Made larger
                            uri = uri,
                            onClick = { dialogState.value = uri }
                        )
                    }

                    // Fill the row with empty space if less than 3 in this row
                    repeat(3 - rowImages.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    dialogState.value?.let { imageUri ->
        ImageDialog(
            imageUri = imageUri,
            takenImageUris = selectedContentImageUris,
            selectedImageIndex = remember { mutableIntStateOf(0) },
            dismissDialog = dismissDialog
        )
    }
}


@Composable
fun rememberPermissionState(permission: String, context: Context): MutableState<Boolean> {
    return remember(context) {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
}

@Composable
fun ContentImage(modifier: Modifier, uri: Uri?, onClick: () -> Unit) {
    if (uri != null && uri != Uri.EMPTY) {
        AddItemContentImageNotNull(modifier, uri, onClick)
    } else {
        AddItemContentImageNull(modifier)
    }
}

private fun createImageUri(context: Context): Uri? {
    val fileName = "temp_capture_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}