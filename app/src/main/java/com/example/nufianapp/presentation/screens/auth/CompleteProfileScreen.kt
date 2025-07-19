package com.example.nufianapp.presentation.screens.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    userViewModel: UserViewModel,
    onProfileSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    var user by remember {
        mutableStateOf(
            com.example.nufianapp.data.model.User(
                uid = firebaseUser?.uid.orEmpty(),
                email = firebaseUser?.email.orEmpty(),
                batch = 0,
                status = "Active Student" // Default status
            )
        )
    }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var expandedBatch by remember { mutableStateOf(false) }
    var isAlumni by remember { mutableStateOf<Boolean?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val processedUri = processImage(context, uri)
                    user = user.copy(avatarUri = processedUri.toString())
                } catch (e: Exception) {
                    error = "Failed to process image: ${e.localizedMessage}"
                }
            }
        }
    }

    val batchYears = (2012..2050).toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set Up Your Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            user.avatarUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Add Profile Photo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap to select profile photo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = user.displayName,
            onValueChange = { user = user.copy(displayName = it) },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Your Study Program", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InterestPill("TI", user.interest == "TI", Blue) {
                user = user.copy(interest = "TI")
            }
            InterestPill("SI", user.interest == "SI", Orange) {
                user = user.copy(interest = "SI")
            }
            InterestPill("BD", user.interest == "BD", Red) {
                user = user.copy(interest = "BD")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Batch (Year) Selection
        Text("Select Your Batch Year", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedBatch,
            onExpandedChange = { expandedBatch = !expandedBatch }
        ) {
            OutlinedTextField(
                value = if (user.batch > 0) user.batch.toString() else "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Batch Year") },
                placeholder = { Text("Select your batch year") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(),
                isError = user.batch == 0
            )

            ExposedDropdownMenu(
                expanded = expandedBatch,
                onDismissRequest = { expandedBatch = false }
            ) {
                batchYears.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            user = user.copy(batch = year)
                            expandedBatch = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alumni Status Selection
        Text("Are you an Alumni?", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Yes Option
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isAlumni == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isAlumni == true) 4.dp else 0.dp,
                border = if (isAlumni == null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        isAlumni = true
                        user = user.copy(status = "Alumni")
                    }
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Yes",
                        color = if (isAlumni == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // No Option
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isAlumni == false) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isAlumni == false) 4.dp else 0.dp,
                border = if (isAlumni == null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clickable {
                        isAlumni = false
                        user = user.copy(status = "Active Student")
                    }
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No",
                        color = if (isAlumni == false) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                error = null

                if (firebaseUser == null) {
                    error = "User not found"
                    return@Button
                }

                if (user.displayName.isBlank() || user.interest.isBlank() || user.batch == 0 || isAlumni == null) {
                    error = "All fields are required"
                    return@Button
                }

                scope.launch {
                    loading = true

                    try {
                        val avatarUri = user.avatarUri
                        val imageUrl = if (avatarUri != null) {
                            val result = userViewModel.uploadImage(user.uid, avatarUri)
                            suspendCoroutine<String?> { continuation ->
                                result.observeForever { url ->
                                    continuation.resume(url)
                                }
                            }
                        } else null

                        val updatedUser = user.copy(
                            avatarUrl = imageUrl,
                            createdAt = user.createdAt
                        )

                        userViewModel.updateUser(updatedUser)
                        loading = false
                        onProfileSaved()
                    } catch (e: Exception) {
                        loading = false
                        error = "Failed to save profile: ${e.localizedMessage}"
                    }
                }
            },
            enabled = user.displayName.isNotBlank() && user.interest.isNotBlank() && user.batch > 0 && isAlumni != null && !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving...")
            } else {
                Text("Save Profile")
            }
        }
    }
}


suspend fun processImage(context: Context, uri: Uri): Uri = withContext(Dispatchers.IO) {
    // Create a temporary file
    val tempFile = File.createTempFile("compressed_image", ".jpg", context.cacheDir)

    // Get input stream from the selected image URI
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("Failed to open input stream")

    // Decode the bitmap to get dimensions
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeStream(inputStream, null, options)
    inputStream.close()

    // Calculate sample size for initial downsampling if needed
    val sampleSize = calculateSampleSize(options.outWidth, options.outHeight)

    // Now actually decode the bitmap with appropriate sample size
    val decodingOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
    }

    val newInputStream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("Failed to open input stream")
    val bitmap = BitmapFactory.decodeStream(newInputStream, null, decodingOptions)
    newInputStream.close()

    bitmap ?: throw Exception("Failed to decode image")

    // Compress bitmap to JPEG with appropriate quality to stay under 2MB
    val outputStream = ByteArrayOutputStream()
    var quality = 90
    var compressedData: ByteArray

    do {
        outputStream.reset() // Clear output stream
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        compressedData = outputStream.toByteArray()

        // If still too large, reduce quality and try again
        if (compressedData.size > 2 * 1024 * 1024) {
            quality -= 10
        }
    } while (compressedData.size > 2 * 1024 * 1024 && quality > 10)

    // Write the compressed data to the temp file
    FileOutputStream(tempFile).use { fos ->
        fos.write(compressedData)
    }

    // Recycle bitmap to free memory
    bitmap.recycle()

    // Return a URI to the temp file
    Uri.fromFile(tempFile)
}

private fun calculateSampleSize(width: Int, height: Int): Int {
    val maxDimension = 1200
    return when {
        width > height && width > maxDimension -> width / maxDimension
        height > width && height > maxDimension -> height / maxDimension
        else -> 1
    }
}

@Composable
fun InterestPill(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}