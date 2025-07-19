package com.example.nufianapp.presentation.screens.notification.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.presentation.core.Utils
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.components.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.notification.viewmodel.NotificationItem
import com.example.nufianapp.presentation.screens.notification.viewmodel.NotificationViewModel
import com.example.nufianapp.ui.theme.Red

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = snackBarHostState) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = modifier,
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                content = {
                    item {
                        Utils().SpacerHeightLarge()
                        RowWithNavigationButton(
                            navigateBack = navigateBack,
                            onClearAllRequest = { showConfirmDialog = true }
                        )
                        Utils().SpacerHeightLarge()
                    }
                    items(lazyPagingItems.itemCount) { index ->
                        lazyPagingItems[index]?.let { notification ->
                            NotificationItem(notification = notification)
                        }
                    }

                    // Handle different load states
                    lazyPagingItems.apply {
                        when {
                            loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                                item {
                                    ContentResponseLoading()
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item {
                                    ContentResponseLoading()
                                }
                            }

                            loadState.refresh is LoadState.Error -> {
                                item {
                                    val errorMessage = "Error loading data"
                                    viewModel.onSnackBarShown(errorMessage)
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
                                    val errorMessage = "Error loading more data"
                                    viewModel.onSnackBarShown(errorMessage)
                                }
                            }

                            loadState.append is LoadState.NotLoading && loadState.refresh !is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                                item {
                                    ContentResponseNull()
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    if (showConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Clear All Notifications",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Red
                )
            },
            text = {
                Text("Are you sure you want to delete all notifications? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.clearAllNotifications()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun RowWithNavigationButton(
    navigateBack: () -> Unit,
    onClearAllRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonIcon(onClickButton = navigateBack, tint = Black)
            Utils().SpacerWidthMedium()
            Text(
                text = "Heads-up",
                style = MaterialTheme.typography.headlineMedium,
                color = Black
            )
        }

        TextButton(onClick = onClearAllRequest) {
            Text(
                "Clear",
                color = Red,
                fontWeight = FontWeight.Bold)
        }
    }
}

