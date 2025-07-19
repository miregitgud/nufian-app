package com.example.nufianapp.presentation.screens.news.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.data.model.News
import com.example.nufianapp.presentation.screens.news.add.components.AddNewsContent
import com.example.nufianapp.presentation.screens.news.add.viewmodel.AddNews
import com.example.nufianapp.presentation.screens.news.add.viewmodel.AddNewsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddNewsScreen(
    viewModel: AddNewsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToNews: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val getContentImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris -> viewModel.handleGetContentImagesResult(uris) }

    // Local state
    var news by remember { mutableStateOf(News()) }
    val selectedContentImageUris by viewModel.selectedContentImageUris.collectAsState()

    val onNewsDataChanged: (News) -> Unit = { updatedNews ->
        news = updatedNews
    }
    // Context
    val context = LocalContext.current

    // Activity result contracts
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.handleTakePictureResult(success)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .background(Color.White)
        ) {
            AddNewsContent(
                onNewsDataChanged = onNewsDataChanged,
                addNews = {
                    scope.launch {
                        viewModel.addNews(news)
                    }
                },
                takePictureLauncher = { uri ->
                    viewModel.updateCapturedImageUri(uri)
                    takePictureLauncher.launch(uri)
                },
                selectedContentImageUris = selectedContentImageUris,
                navigateBack = navigateBack,
                getContentImagesLauncher = getContentImagesLauncher,
                context = context
            )
        }
    }

    AddNews(navigateToNews = navigateToNews)
    // Observe snackBar messages
    LaunchedEffect(viewModel.snackBarFlow) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }
}