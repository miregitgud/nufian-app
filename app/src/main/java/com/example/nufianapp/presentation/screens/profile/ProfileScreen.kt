package com.example.nufianapp.presentation.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.profile.components.ProfileContent
import com.example.nufianapp.presentation.screens.profile.viewmodel.ProfileViewModel
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navigateToSignIn: () -> Unit,
    navigateBack: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToAddCertificate: () -> Unit,
    navigateToAddProject: () -> Unit,
    navigateToDetailProject: (String, String) -> Unit,
    navigateToDetail: (String, String, Boolean) -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val userDataResponse by userViewModel.userByIdData.collectAsState()
    var currentPage by remember { mutableIntStateOf(0) } // State to track current page

    LaunchedEffect(userViewModel.currentUserId) {
        userViewModel.currentUserId?.let {
            userViewModel.fetchUserById(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (currentPage != 0) {
                FloatingActionButton(
                    onClick = {
                        when (currentPage) {
                            1 -> {
                                navigateToAddCertificate()
                            }

                            2 -> {
                                navigateToAddProject()
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = Blue,
                    contentColor = Color.White,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_item_add),
                        contentDescription = null
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(scaffoldState) }
    ) {
        Box(modifier = Modifier) {
            when (userDataResponse) {
                is Response.Loading -> {
                    ContentResponseLoading()
                }

                is Response.Success -> {
                    val user = (userDataResponse as Response.Success).data

                    // Use ProfileGradientBackground with the user's interest
                    if (user != null) {
                        ProfileGradientBackground(user = user) {
                            ProfileContent(
                                navigateToSetting = navigateToSetting,
                                navigateBack = navigateBack,
                                navigateToSignIn = navigateToSignIn,
                                isProfileView = true,
                                user = user,
                                onPageChange = { page -> currentPage = page },
                                isProfile = true,
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
    }
}

@Composable
fun ProfileGradientBackground(
    user: User,
    content: @Composable () -> Unit
) {
    val userInterest = user.interest.lowercase() ?: ""

    val gradientColors = when {
        userInterest.contains("ti") -> listOf(Color.Transparent, Blue.copy(alpha = 0.1f), Blue.copy(alpha = 0.2f))
        userInterest.contains("si") -> listOf(Color.Transparent, Orange.copy(alpha = 0.1f), Orange.copy(alpha = 0.2f))
        userInterest.contains("bd") -> listOf(Color.Transparent, Red.copy(alpha = 0.1f), Red.copy(alpha = 0.2f))
        userInterest.contains("admin") -> listOf(Color.Transparent, Charcoal.copy(alpha = 0.1f), Charcoal.copy(alpha = 0.1f))
        else -> listOf(Color.Transparent, Color.Transparent, Color.Transparent) // Default to blue
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors,
                        startY = 300f,
                        endY = 1800f
                    )
                )
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            content()
        }
    }
}