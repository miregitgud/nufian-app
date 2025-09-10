package com.example.nufianapp.presentation.screens.discover

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.presentation.core.Constants.IMAGES_PATTERN
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.BaseLayoutHome
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.discover.components.ConnectItemHeadline
import com.example.nufianapp.presentation.screens.discover.viewmodel.ConnectUserViewModel
import com.example.nufianapp.presentation.screens.discover.components.ConnectItem
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.NeonWhite

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DiscoverBaseScreen(
    modifier: Modifier = Modifier,
    navigateToConnectProfile: (String) -> Unit,
    connectViewModel: ConnectUserViewModel = hiltViewModel(),
    navigateToProfile: () -> Unit,
) {
    val searchText by connectViewModel.searchText.collectAsState()
    val lazyPagingItems = connectViewModel.pagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    val currentUser by connectViewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = snackBarHostState) {
        connectViewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        BaseLayoutHome()

        Column(modifier = modifier.padding(vertical = 20.dp)) {
            Utils().SpacerHeightLarge()
            ConnectItemHeadline()
            Utils().SpacerHeightLarge()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Our Users",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp)) // optional padding between title and search

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newText -> connectViewModel.updateSearchText(newText) },
                    placeholder = { Text("Search") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { connectViewModel.clearSearchText() }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Charcoal,
                        unfocusedTextColor = DisabledColor,
                        disabledTextColor = DisabledColor,
                        focusedContainerColor = NeonWhite,
                        unfocusedContainerColor = NeonWhite,
                        cursorColor = Charcoal,
                        focusedLeadingIconColor = Charcoal,
                        unfocusedLeadingIconColor = DisabledColor,
                        focusedTrailingIconColor = Charcoal,
                        unfocusedTrailingIconColor = DisabledColor,
                        focusedPlaceholderColor = Charcoal,
                        unfocusedPlaceholderColor = DisabledColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable LazyColumn
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 150.dp
                ),
                content = {
                    if (currentUser != null) {
                        items(lazyPagingItems.itemCount) { index ->
                            val imageId = IMAGES_PATTERN[index % IMAGES_PATTERN.size]
                            lazyPagingItems[index]?.let { user ->

                                val isAdmin = currentUser?.userType?.equals("admin", ignoreCase = true) == true
                                val isUserBanned = user.isBanned ?: false

                                // Admins see everyone, non-admins don't see banned users
                                if (!isUserBanned || isAdmin) {
                                    ConnectItem(
                                        user = user,
                                        imageId = imageId,
                                        currentUserType = currentUser?.userType.orEmpty(),
                                        modifier = modifier.clickable {
                                            if (user.uid == currentUser?.uid) navigateToProfile() else navigateToConnectProfile(user.uid)
                                        },
                                        onBanUnbanConfirmed = { shouldBan ->
                                            if (shouldBan) {
                                                connectViewModel.banUser(user.uid)
                                            } else {
                                                connectViewModel.unbanUser(user.uid)
                                            }
                                        }
                                    )
                                }
                            }
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
                                    connectViewModel.onSnackBarShown(errorMessage)
                                }
                            }

                            loadState.append is LoadState.Error -> {
                                item {
                                    val errorMessage = "Error loading more data"
                                    connectViewModel.onSnackBarShown(errorMessage)
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
}

