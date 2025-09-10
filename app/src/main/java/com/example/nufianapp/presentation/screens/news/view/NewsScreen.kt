package com.example.nufianapp.presentation.screens.news.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.R
import com.example.nufianapp.data.model.News
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.BaseLayoutHome
import com.example.nufianapp.presentation.core.components.CustomDeleteDialog
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.home.view.viewmodel.HomeViewModel
import com.example.nufianapp.presentation.screens.news.view.components.NewsItemHeader
import com.example.nufianapp.presentation.screens.news.view.viewmodel.NewsItem
import com.example.nufianapp.presentation.screens.news.view.viewmodel.NewsViewModel
import com.example.nufianapp.ui.theme.Blue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsScreen(
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    navigateToAddNews: () -> Unit,
    viewModel: NewsViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    val isAdmin = viewModel.isAdmin.collectAsState().value
    val showDeleteDialog = remember { mutableStateOf(false) }
    val newsToDelete = remember { mutableStateOf<News?>(null) }
    val currentUser by homeViewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = snackBarHostState) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    if (showDeleteDialog.value) {
        when {
            newsToDelete.value != null -> {
                CustomDeleteDialog(
                    showDialog = showDeleteDialog,
                    title = "Delete News",
                    message = "Are you sure you want to delete this news?",
                    onConfirm = {
                        newsToDelete.value?.let {
                            viewModel.deleteNews(it.newsId)
                            lazyPagingItems.refresh()
                        }
                        showDeleteDialog.value = false
                        newsToDelete.value = null
                    },
                    onDismiss = {
                        showDeleteDialog.value = false
                        newsToDelete.value = null
                    }
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { navigateToAddNews() },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = Blue,
                    modifier = Modifier
                        .offset(y = (-130).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_item_add),
                        contentDescription = null
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            BaseLayoutHome()

            Column {
                Utils().SpacerHeightLarge()
                NewsItemHeader(
                )
                LazyColumn(
                    modifier = Modifier.padding(top = 20.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 150.dp
                    ),
                    content = {
                        items(lazyPagingItems.itemCount) { index ->
                            lazyPagingItems[index]?.let { news ->
                                NewsItem(
                                    news = news,
                                    modifier = modifier.clickable {
                                        navigateToDetail(news.newsId)
                                    },
                                    onDeleteNews = {
                                        newsToDelete.value = news
                                        showDeleteDialog.value = true
                                    },
                                    user = currentUser
                                )
                            }
                        }

                        // Load state handling
                        lazyPagingItems.apply {
                            when {
                                loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                                    item { ContentResponseLoading() }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item { ContentResponseLoading() }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    item {
                                        viewModel.onSnackBarShown("Error loading data")
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    item {
                                        viewModel.onSnackBarShown("Error loading more data")
                                    }
                                }

                                loadState.append is LoadState.NotLoading &&
                                        loadState.refresh !is LoadState.Loading &&
                                        itemCount == 0 -> {
                                    item { ContentResponseNull() }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
