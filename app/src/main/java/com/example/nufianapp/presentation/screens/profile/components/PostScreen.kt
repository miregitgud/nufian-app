package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.clickable
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
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.content.ContentResponseNull

@Composable
fun PostScreen(
    onDeletePost: (Forum) -> Unit,
    lazyPagingItems: LazyPagingItems<Forum>,
    showSnackBar: (String) -> Unit,
    isProfile: Boolean = false,
    navigateToDetail: (String, String, Boolean) -> Unit,
) {
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
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
                    lazyPagingItems[index]?.let { post ->
                        PostItem(
                            forum = post,
                            isProfile = isProfile,
                            onDeletePost = onDeletePost,
                            modifier = Modifier
                                .clickable {
                                    navigateToDetail(
                                        post.forumId,
                                        post.forumUserPostId,
                                        false
                                    )
                                }
                        )
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