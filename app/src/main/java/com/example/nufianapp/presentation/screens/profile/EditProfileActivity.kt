package com.example.nufianapp.presentation.screens.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.auth.InterestPill
import com.example.nufianapp.presentation.screens.profile.components.EditTextField
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.NeonWhite
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileActivity(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    navigateToProfile: () -> Unit,
    navigateBack: () -> Unit
) {
    val userId = userViewModel.currentUserId
    val context = LocalContext.current

    LaunchedEffect(userId) {
        if (userId != null) {
            userViewModel.fetchUserById(userId)
        }
    }

    val userResponse by userViewModel.userByIdData.collectAsState()

    var name by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var bioData by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var linkedInUrl by remember { mutableStateOf("") }
    var instagramUrl by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf("Active Student") }
    var initialized by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var avatarBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var expandedBatch by remember { mutableStateOf(false) }
    var isAlumni by remember { mutableStateOf<Boolean?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                userViewModel.uploadImage(
                    userId!!,
                    uri.toString()
                ).observe(context as LifecycleOwner) { downloadUrl ->
                    if (downloadUrl.isNotEmpty()) {
                        avatarUrl = downloadUrl
                        avatarBitmap = null
                    }
                }
            }
        }

    val batchYears = (2012..2050).toList()

    when (userResponse) {
        is Response.Loading -> {
            ContentResponseLoading()
        }

        is Response.Success -> {
            val user = (userResponse as Response.Success<User?>).data
            if (user != null && !initialized) {
                name = user.displayName
                interest = user.interest
                bioData = user.bioData
                avatarUrl = user.avatarUrl ?: ""
                linkedInUrl = user.linkedinUrl ?: ""
                instagramUrl = user.instagramUrl ?: ""
                batch = user.batch
                status = user.status
                isAlumni = when (user.status) {
                    "Alumni" -> true
                    "Active Student" -> false
                    else -> null
                }
                initialized = true
            }

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(NeonWhite)
                    .systemBarsPadding()
            ) {
                // Top bar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 21.dp, top = 20.dp)
                        .requiredSize(49.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    ButtonIcon(onClickButton = navigateBack)
                }

                Text(
                    text = "Edit Profile",
                    color = Color(0xff000000),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Image(
                            painter = if (avatarUrl.isEmpty()) painterResource(id = R.drawable.img_avatar_default)
                            else rememberAsyncImagePainter(
                                avatarUrl
                            ),
                            contentDescription = "Intersect",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .requiredSize(105.dp)
                                .clip(CircleShape)
                                .align(Alignment.Center)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(x = 35.dp, y = 45.dp)
                                .requiredSize(35.dp)
                                .clip(CircleShape)
                                .background(color = Color(0xfff0f0f0))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit_light),
                                contentDescription = "Camera Light",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .requiredSize(20.dp)
                                    .clickable { imagePickerLauncher.launch("image/*") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (interest == "Admin") {
                        EditTextField(
                            label = "Display Name",
                            value = name,
                            onValueChange = { if (it.length <= 25) name = it })

                        Spacer(modifier = Modifier.height(8.dp))

                        EditTextField(
                            label = "Biodata",
                            value = bioData,
                            onValueChange = { if (it.length <= 500) bioData = it })
                        EditTextField(
                            label = "LinkedIn URL",
                            value = linkedInUrl,
                            onValueChange = { linkedInUrl = it })
                        EditTextField(
                            label = "Instagram URL",
                            value = instagramUrl,
                            onValueChange = { instagramUrl = it })

                        // Save button
                        Button(
                            onClick = {
                                if (userId != null) {
                                    val existingUser =
                                        (userResponse as? Response.Success<User?>)?.data
                                    if (existingUser != null) {
                                        val updatedUser = existingUser.copy(
                                            displayName = name,
                                            interest = interest,
                                            bioData = bioData,
                                            linkedinUrl = linkedInUrl,
                                            instagramUrl = instagramUrl,
                                            avatarUrl = avatarUrl,
                                            batch = batch,
                                            status = status
                                        )

                                        userViewModel.updateUser(updatedUser)
                                        Log.d(
                                            "EditProfileActivity",
                                            "Updating user with ID: ${updatedUser.uid}"
                                        )
                                        navigateToProfile()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .padding(vertical = 30.dp)
                                .align(Alignment.CenterHorizontally)
                                .requiredHeight(48.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(color = Color(0xff4986ea))
                        ) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    } else {
                        EditTextField(
                            label = "Display Name",
                            value = name,
                            onValueChange = { if (it.length <= 25) name = it })

                        Text(
                            text = "Select Your Study Program",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 24.dp, top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            InterestPill("TI", interest == "TI", Blue) {
                                interest = "TI"
                            }
                            InterestPill("SI", interest == "SI", Orange) {
                                interest = "SI"
                            }
                            InterestPill("BD", interest == "BD", Red) {
                                interest = "BD"
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Batch (Year) Selection
                        Text(
                            text = "Select Your Batch Year",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedBatch,
                            onExpandedChange = { expandedBatch = !expandedBatch },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            OutlinedTextField(
                                value = if (batch > 0) batch.toString() else "",
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
                                isError = batch == 0
                            )

                            ExposedDropdownMenu(
                                expanded = expandedBatch,
                                onDismissRequest = { expandedBatch = false }
                            ) {
                                batchYears.forEach { year ->
                                    DropdownMenuItem(
                                        text = { Text(year.toString()) },
                                        onClick = {
                                            batch = year
                                            expandedBatch = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Alumni Status Selection
                        Text(
                            text = "Are you an Alumni?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Yes Option
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isAlumni == true) Blue else Color(0xfff0f0f0),
                                tonalElevation = if (isAlumni == true) 4.dp else 0.dp,
                                border = BorderStroke(1.dp, Blue),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        isAlumni = true
                                        status = "Alumni"
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Yes",
                                        color = if (isAlumni == true) Color.White else Color.Black,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            // No Option
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isAlumni == false) Blue else Color(0xfff0f0f0),
                                tonalElevation = if (isAlumni == false) 4.dp else 0.dp,
                                border = BorderStroke(1.dp, Blue),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        isAlumni = false
                                        status = "Active Student"
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No",
                                        color = if (isAlumni == false) Color.White else Color.Black,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        EditTextField(
                            label = "Biodata",
                            value = bioData,
                            onValueChange = { if (it.length <= 500) bioData = it })
                        EditTextField(
                            label = "LinkedIn URL",
                            value = linkedInUrl,
                            onValueChange = { linkedInUrl = it })
                        EditTextField(
                            label = "Instagram URL",
                            value = instagramUrl,
                            onValueChange = { instagramUrl = it })

                        // Save button
                        Button(
                            onClick = {
                                if (userId != null) {
                                    val existingUser =
                                        (userResponse as? Response.Success<User?>)?.data
                                    if (existingUser != null) {
                                        val updatedUser = existingUser.copy(
                                            displayName = name,
                                            interest = interest,
                                            bioData = bioData,
                                            linkedinUrl = linkedInUrl,
                                            instagramUrl = instagramUrl,
                                            avatarUrl = avatarUrl,
                                            batch = batch,
                                            status = status
                                        )

                                        userViewModel.updateUser(updatedUser)
                                        Log.d(
                                            "EditProfileActivity",
                                            "Updating user with ID: ${updatedUser.uid}"
                                        )
                                        navigateToProfile()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(300.dp)
                                .padding(vertical = 30.dp)
                                .align(Alignment.CenterHorizontally)
                                .requiredHeight(48.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(color = Color(0xff4986ea))
                        ) {
                            Text(
                                text = "Save",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }

        }

        is Response.Failure -> {

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EditProfileActivityPreview() {
    EditProfileActivity(Modifier, navigateToProfile = {}, navigateBack = {})
}