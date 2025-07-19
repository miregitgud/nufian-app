package com.example.nufianapp.presentation.screens.home.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.presentation.screens.home.add.components.AddForumContent
import com.example.nufianapp.presentation.screens.home.add.viewmodel.AddForum
import com.example.nufianapp.presentation.screens.home.add.viewmodel.AddForumViewModel
import com.example.nufianapp.ui.theme.Blue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddForumScreen(
    navigateToForum: () -> Unit,
    navigateBack: () -> Unit,
    viewModel: AddForumViewModel = hiltViewModel(),
) {
    // Collecting state from ViewModel
    val categories = viewModel.categories
    val selectedForumCategory by viewModel.selectedForumCategory.collectAsState()
    val selectedContentImageUris by viewModel.selectedContentImageUris.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Context
    val context = LocalContext.current

    // Activity result contracts
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.handleTakePictureResult(success)
    }

    val getContentImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.handleGetContentImagesResult(uris) }

    // Local state
    var forum by remember { mutableStateOf(Forum()) }

    val onForumDataChanged: (Forum) -> Unit = { updatedForum ->
        forum = updatedForum
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            Box(modifier = Modifier.shadow(4.dp)) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Default.Close, contentDescription = "Back")
                        }
                    },
                    actions = {
                        SaveButton(onClick = {
                            scope.launch {
                                viewModel.addForum(forum)
                            }
                        })
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        navigationIconContentColor = Color.Black
                    )
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .background(Color.White)
        ) {
            // Content of AddForumScreen
            AddForumContent(
                onForumDataChanged = onForumDataChanged,
                categories = categories,
                selectedForumCategory = selectedForumCategory,
                selectedContentImageUris = selectedContentImageUris,
                context = context,
                takePictureLauncher = { uri ->
                    viewModel.updateCapturedImageUri(uri)
                    takePictureLauncher.launch(uri)
                },
                getContentImagesLauncher = getContentImagesLauncher,
                viewModel = viewModel,
            )
        }
    }

    AddForum(navigateToForum = navigateToForum)
    // Observe snackBar messages
    LaunchedEffect(viewModel.snackBarFlow) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        shape = ButtonDefaults.shape,
        elevation = ButtonDefaults.buttonElevation(4.dp),
        colors = ButtonDefaults.buttonColors(Blue),
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(text = "Post", color = Color.White)
    }
}
