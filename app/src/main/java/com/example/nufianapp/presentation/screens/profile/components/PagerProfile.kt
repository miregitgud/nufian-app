package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.presentation.core.components.CustomDeleteDialog
import com.example.nufianapp.presentation.screens.profile.CertificateScreen
import com.example.nufianapp.presentation.screens.profile.ProjectScreen
import com.example.nufianapp.presentation.screens.profile.viewmodel.CertificateViewModel
import com.example.nufianapp.presentation.screens.profile.viewmodel.PostViewModel
import com.example.nufianapp.presentation.screens.profile.viewmodel.ProjectViewModel

@Composable
fun PagerProfile(
    onPageChange: (Int) -> Unit,
    certificateViewModel: CertificateViewModel = hiltViewModel(),
    projectViewModel: ProjectViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel(),
    isProfile: Boolean = false,
    userId: String? = "",
    navigateToDetailProject: (String, String) -> Unit,
    navigateToDetail: (String, String, Boolean) -> Unit,
) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val certificateToDelete = remember { mutableStateOf<Certificate?>(null) }
    val projectToDelete = remember { mutableStateOf<Project?>(null) }
    val postToDelete = remember { mutableStateOf<Forum?>(null) }

    if (showDeleteDialog.value) {
        when {
            certificateToDelete.value != null -> {
                CustomDeleteDialog(
                    showDialog = showDeleteDialog,
                    title = "Delete Certificate",
                    message = "Are you sure you want to delete this certificate?",
                    onConfirm = {
                        certificateToDelete.value?.let {
                            certificateViewModel.deleteCertificate(it.certificateId)
                        }
                        showDeleteDialog.value = false
                        certificateToDelete.value = null
                    },
                    onDismiss = {
                        showDeleteDialog.value = false
                        certificateToDelete.value = null
                    }
                )
            }

            projectToDelete.value != null -> {
                CustomDeleteDialog(
                    showDialog = showDeleteDialog,
                    title = "Delete Project",
                    message = "Are you sure you want to delete this project?",
                    onConfirm = {
                        projectToDelete.value?.let {
                            projectViewModel.deleteProject(it.projectId)
                        }
                        showDeleteDialog.value = false
                        projectToDelete.value = null
                    },
                    onDismiss = {
                        showDeleteDialog.value = false
                        projectToDelete.value = null
                    }
                )
            }

            postToDelete.value != null -> {
                CustomDeleteDialog(
                    showDialog = showDeleteDialog,
                    title = "Delete Post",
                    message = "Are you sure you want to delete this post?",
                    onConfirm = {
                        postToDelete.value?.let {
                            postViewModel.deletePost(it.forumId)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Define tab items with their respective icons (outline and filled variants)
        data class TabItem(
            val outlineIconResId: Int,
            val filledIconResId: Int,
            val title: String = ""
        )

        val tabItems = listOf(
            TabItem(R.drawable.ic_post_outline, R.drawable.ic_post_filled, "Posts"),
            TabItem(R.drawable.ic_certificate_outline, R.drawable.ic_certificate_filled, "Certificates"),
            TabItem(R.drawable.ic_project_outline, R.drawable.ic_project_filled, "Projects")
        )

        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState { tabItems.size }

        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }

        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex = pagerState.currentPage
                onPageChange(pagerState.currentPage)
            }
        }

        Box(
            modifier = Modifier
                .width(300.dp)
                .height(70.dp)
                .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .zIndex(1f)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color.Transparent
                    )
                },
                divider = {}
            ) {
                tabItems.forEachIndexed { index, tabItem ->
                    val selected = index == selectedTabIndex
                    Tab(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(
                                    id = if (selected) tabItem.filledIconResId else tabItem.outlineIconResId
                                ),
                                contentDescription = null,
                                tint = Color(0xFF4986EA),
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(bottom = 6.dp)
                            )

                            // Tab title text
                            androidx.compose.material3.Text(
                                text = tabItem.title,
                                color = if (selected) Color(0xFF4986EA) else Color.Gray,
                                style = MaterialTheme.typography.labelSmall
                            )

                            // Triangle indicator
                            if (selected) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_triangle),
                                    contentDescription = null,
                                    tint = Color(0xFF4986EA),
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { index ->
            when (index) {
                0 -> {
                    val lazyPagingItems = postViewModel.pagingFlow.collectAsLazyPagingItems()
                    LaunchedEffect(Unit) {
                        postViewModel.getPagingFlow(isProfile, userId)
                    }
                    PostScreen(
                        onDeletePost = { post ->
                            postToDelete.value = post
                            showDeleteDialog.value = true
                        },
                        lazyPagingItems = lazyPagingItems,
                        showSnackBar = { message -> postViewModel.onSnackBarShown(message) },
                        isProfile = isProfile,
                        navigateToDetail = navigateToDetail
                    )
                }

                1 -> {
                    val lazyPagingItems = certificateViewModel.pagingFlow.collectAsLazyPagingItems()
                    LaunchedEffect(Unit) {
                        certificateViewModel.getPagingFlow(isProfile, userId)
                    }

                    CertificateScreen(
                        onDeleteCertificate = { certificate ->
                            certificateToDelete.value = certificate
                            showDeleteDialog.value = true
                        },
                        lazyPagingItems = lazyPagingItems,
                        showSnackBar = { message -> certificateViewModel.onSnackBarShown(message) },
                        isProfile = isProfile
                    )
                }

                2 -> {
                    val lazyPagingItems = projectViewModel.pagingFlow.collectAsLazyPagingItems()
                    LaunchedEffect(Unit) {
                        projectViewModel.getPagingFlow(isProfile, userId)
                    }

                    ProjectScreen(
                        onDeleteProject = { project ->
                            projectToDelete.value = project
                            showDeleteDialog.value = true
                        },
                        lazyPagingItems = lazyPagingItems,
                        showSnackBar = { message -> projectViewModel.onSnackBarShown(message)},
                        isProfile = isProfile,
                        navigateToDetailProject = navigateToDetailProject
                    )
                }
            }
        }
    }
}