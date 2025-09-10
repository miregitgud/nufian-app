package com.example.nufianapp.presentation.screens.profile

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.core.content.ContentResponseNull
import com.example.nufianapp.presentation.screens.profile.components.CertificateDetailScreen
import com.example.nufianapp.presentation.screens.profile.components.CertificateItem

@Composable
fun CertificateScreen(
    onDeleteCertificate: (Certificate) -> Unit,
    lazyPagingItems: LazyPagingItems<Certificate>,
    showSnackBar: (String) -> Unit,
    isProfile: Boolean = false,
) {
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }
    var selectedCertificate by remember { mutableStateOf<Certificate?>(null) }

    if (selectedCertificate != null) {
        CertificateDetailScreen(
            certificate = selectedCertificate!!,
            onBack = { selectedCertificate = null }
        )
    } else {
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
                        lazyPagingItems[index]?.let { certificate ->
                            CertificateItem(
                                certificate = certificate,
                                isProfile = isProfile,
                                onDeleteCertificate = onDeleteCertificate,
                                onViewCertificate = { selectedCertificate = it }
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
}