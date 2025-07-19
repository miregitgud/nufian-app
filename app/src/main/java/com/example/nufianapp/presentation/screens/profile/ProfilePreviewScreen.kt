package com.example.nufianapp.presentation.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.profile.components.ProfileContent
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePreviewScreen(
    navigateBack: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToDetailProject: (String, String) -> Unit,
    navigateToDetail: (String, String, Boolean) -> Unit,
    userId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val scaffoldState = remember { SnackbarHostState() }
    val userResponse by userViewModel.userByIdData.collectAsState()

    LaunchedEffect(userId) {
        userViewModel.fetchUserById(userId)
    }

    Scaffold(
        content = {
            Box(modifier = Modifier.padding()) {
                when (userResponse) {
                    is Response.Loading -> {
                        ContentResponseLoading()
                    }

                    is Response.Success -> {
                        val user = (userResponse as? Response.Success)?.data
                        if (user != null) {
                            ProfileGradientBackground(user = user) {
                                ProfileContent(
                                    navigateToSetting = navigateToSetting,
                                    navigateBack = navigateBack,
                                    isProfileView = false,
                                    user = user,
                                    isProfile = false,
                                    navigateToDetailProject = navigateToDetailProject,
                                    navigateToDetail = navigateToDetail
                                )
                            }
                        }
                    }

                    is Response.Failure -> {
                        Text(
                            text = "An error occurred. Please try again later.",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(scaffoldState) }
    )
}
