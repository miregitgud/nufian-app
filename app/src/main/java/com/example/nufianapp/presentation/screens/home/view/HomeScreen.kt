

package com.example.nufianapp.presentation.screens.home.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.presentation.core.PullToRefreshLazyColumn
import com.example.nufianapp.presentation.core.components.BaseLayoutHome
import com.example.nufianapp.presentation.core.components.CustomDeleteDialog
import com.example.nufianapp.presentation.core.components.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.home.view.components.HomeItemAnimatedText
import com.example.nufianapp.presentation.screens.home.view.components.HomeItemCategoryHeader
import com.example.nufianapp.presentation.screens.home.view.components.WelcomeScreen
import com.example.nufianapp.presentation.screens.home.view.viewmodel.HomeViewModel
import com.example.nufianapp.presentation.screens.home.view.viewmodel.components.CategoryList
import com.example.nufianapp.presentation.screens.home.view.viewmodel.components.ForumItem
import com.example.nufianapp.presentation.screens.profile.viewmodel.PostViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private object WelcomeScreenState {
    var hasShownWelcomeScreen = false
}

@Composable
fun HomeScreen(
    navigateToDetail: (String, String, Boolean) -> Unit,
    navigateToNotification: (String) -> Unit,
    navigateToProfilePreview: (String) -> Unit,
    modifier: Modifier = Modifier,
    postViewModel: PostViewModel = hiltViewModel(),
    viewModel: HomeViewModel = hiltViewModel(),
    showBottomBar: MutableState<Boolean>
) {
    val selectedCategory by viewModel.selectedForumCategory.collectAsState()
    val userMap by viewModel.userMap.collectAsState()
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    val activity = getActivity()
    val coroutineScope = rememberCoroutineScope()

    var showWelcomeScreen by remember { mutableStateOf(false) }
    var showHomeContent by remember { mutableStateOf(false) }
    var showNotificationButton by remember { mutableStateOf(false) }
    var showForumContent by remember { mutableStateOf(false) }

    val isFirstLogin by viewModel.isFirstLogin

    var isRefreshing by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.uid ?: viewModel.getCurrentUserId()
    val userType = userMap[currentUserId]?.userType ?: "student" // fallback

    val greetingTexts = viewModel.getGreetingTexts(currentUser?.displayName?.split(" ")?.firstOrNull() ?: "User")

    val showDeleteDialog = remember { mutableStateOf(false) }
    val certificateToDelete = remember { mutableStateOf<Certificate?>(null) }
    val projectToDelete = remember { mutableStateOf<Project?>(null) }
    val postToDelete = remember { mutableStateOf<Forum?>(null) }


    // Animation values
    val welcomeScreenAlpha by animateFloatAsState(
        targetValue = if (showWelcomeScreen) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "WelcomeScreenAlpha"
    )

    val homeContentAlpha by animateFloatAsState(
        targetValue = if (showHomeContent) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "HomeContentAlpha"
    )

    val forumContentTranslation by animateFloatAsState(
        targetValue = if (showForumContent) 0f else 200f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ForumContentTranslation"
    )

    LaunchedEffect(snackBarHostState) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(selectedCategory) {
        viewModel.selectForumCategory(selectedCategory)
    }

    LaunchedEffect(lazyPagingItems.loadState) {
        isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    }

    LaunchedEffect(snackBarHostState) {
        postViewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }


    val hasFetchedData by viewModel.isFirstFetchDone.collectAsState()

    LaunchedEffect(hasFetchedData) {
        if (!hasFetchedData) {
            viewModel.initialFetchData()
        }
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            Log.d("HomeScreen", "Calling suspend login status for UID: $uid")
            viewModel.loadUserLoginStatusDirect(uid)
        }

        if (!WelcomeScreenState.hasShownWelcomeScreen) {
            showWelcomeScreen = true
            showHomeContent = false
            showNotificationButton = false
            showForumContent = false
            showBottomBar.value = false
        } else {
            showWelcomeScreen = false
            showHomeContent = true
            showNotificationButton = true
            showForumContent = true
            showBottomBar.value = true
        }
    }


    // Function to handle user upward scroll to transition from welcome screen to home screen
    fun handleUpwardScroll() {
        coroutineScope.launch {
            showWelcomeScreen = false
            delay(500)
            showHomeContent = true
            delay(300)
            showNotificationButton = true
            delay(200)
            showForumContent = true
            showBottomBar.value = true

            // Mark as shown in both the global state and the ViewModel
            WelcomeScreenState.hasShownWelcomeScreen = true
            viewModel.markWelcomeScreenShown()
        }
    }

    BackHandler {
        activity?.moveTaskToBack(true)
    }

    fun refreshContent() {
        viewModel.refreshData()
    }

    if (showDeleteDialog.value) {
        when {
            certificateToDelete.value != null -> {
            }

            projectToDelete.value != null -> {
            }

            postToDelete.value != null -> {
                CustomDeleteDialog(
                    showDialog = showDeleteDialog,
                    title = "Delete Post",
                    message = "Are you sure you want to delete this post?",
                    onConfirm = {
                        postToDelete.value?.let {
                            postViewModel.deletePost(it.forumId)
                            lazyPagingItems.refresh()
                        }
                        showDeleteDialog.value = false
                        postToDelete.value = null
                    },
                    onDismiss = {
                        showDeleteDialog.value = false
                        postToDelete.value = null
                    }
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(bottom = 160.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BaseLayoutHome()

            // Welcome Screen - Shown only if it hasn't been shown yet in this app session
            if (!WelcomeScreenState.hasShownWelcomeScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(1f) // Bring to front
                        .alpha(welcomeScreenAlpha)
                ) {
                    WelcomeScreen(
                        userName = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "User",
                        isFirstLogin = isFirstLogin,
                        onUpwardScroll = {
                            handleUpwardScroll()
                            if (currentUser != null) {
                                viewModel.markFirstLoginComplete(currentUser!!.uid)
                            }
                        }
                    )
                }
            }



            // Main Home Content
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .alpha(homeContentAlpha)
            ) {
                PullToRefreshLazyColumn(
                    items = lazyPagingItems.itemSnapshotList.items,
                    content = { forumItem ->
                        val user = forumItem.forumUserPostId.let { userMap[it] }
                        forumItem.let {
                            if (user != null) {
                                ForumItem(
                                    forum = forumItem,
                                    user = user,
                                    onButtonClick = {
                                        navigateToDetail(
                                            forumItem.forumId,
                                            forumItem.forumUserPostId,
                                            true
                                        )
                                    },
                                    modifier = modifier
                                        .clickable {
                                            navigateToDetail(
                                                forumItem.forumId,
                                                forumItem.forumUserPostId,
                                                false
                                            )
                                        }
                                        .graphicsLayer {
                                            // Apply translation animation to forum items
                                            translationY = if (showForumContent) 0f else forumContentTranslation
                                        },
                                    navigateToProfilePreview = navigateToProfilePreview,
                                    onDeletePost = { post ->
                                        postToDelete.value = post
                                        showDeleteDialog.value = true
                                    },
                                    currentUserUid = currentUserId,
                                    userType = userType
                                )
                            }
                        }
                    },
                    isRefreshing = isRefreshing,
                    onRefresh = ::refreshContent,
                    modifier = modifier,
                    lazyListState = listState,
                    contentPadding = PaddingValues(16.dp),
                    header = {
                        Box(modifier = modifier
                            .wrapContentHeight()
                            .padding(10.dp)
                            .zIndex(1f)) {

                            // Show animated text in header only when welcome screen has been shown
                            if (WelcomeScreenState.hasShownWelcomeScreen && currentUser != null) {
                                HomeItemAnimatedText(
                                    firstText = greetingTexts.first,
                                    secondText = greetingTexts.second
                                )
                            }

                            // Notification button with fade-in animation
                            AnimatedVisibility(
                                visible = showNotificationButton,
                                enter = fadeIn(
                                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                                )
                            ) {
                                HomeItemCategoryHeader(
                                    navigateToNotification = navigateToNotification,
                                    userId = currentUserId
                                )
                            }
                        }

                        // Category list with slide-in animation
                        AnimatedVisibility(
                            visible = showForumContent,
                            enter = slideInVertically(
                                initialOffsetY = { 100 },
                                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = 500)
                            )
                        ) {
                            CategoryList(
                                categories = viewModel.categories,
                                selectedCategory = selectedCategory,
                                onCategorySelected = viewModel::selectForumCategory,
                                userType = userType
                            )
                        }
                    },
                    loadingAndErrorHandler = {
                        lazyPagingItems.apply {
                            when {
                                loadState.refresh is LoadState.Error -> {
                                    val errorMessage = "Error loading data"
                                    viewModel.onSnackBarShown(errorMessage)
                                }
                                loadState.append is LoadState.Error -> {
                                    val errorMessage = "Error loading more data"
                                    viewModel.onSnackBarShown(errorMessage)
                                }
                                loadState.append is LoadState.NotLoading && loadState.refresh !is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                                    ContentResponseNull()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun getActivity(): Activity? {
    var context = LocalContext.current
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}