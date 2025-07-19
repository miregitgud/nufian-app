package com.example.nufianapp.presentation.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.components.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.profile.components.ProjectItem

@Composable
fun ProjectScreen(
    onDeleteProject: (Project) -> Unit,
    lazyPagingItems: LazyPagingItems<Project>,
    showSnackBar: (String) -> Unit,
    isProfile: Boolean = false,
    navigateToDetailProject: (String, String) -> Unit
) {
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }


    Scaffold(
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(lazyPagingItems.itemCount) { index ->
                    lazyPagingItems[index]?.let { project ->
                        ProjectItem(
                            project = project,
                            isProfile = isProfile,
                            onDeleteProject = onDeleteProject,
                            navigateToDetailProject = navigateToDetailProject
                        )
                    }
                }

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
                                showSnackBar(errorMessage)
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            item {
                                val errorMessage = "Error loading more data"
                                showSnackBar(errorMessage)
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
        }
    }
}