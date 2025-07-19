package com.example.nufianapp.presentation.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.components.content.ContentResponseError
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.profile.viewmodel.ProfileViewModel
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import com.example.nufianapp.presentation.screens.settings.components.AccountSection
import com.example.nufianapp.presentation.screens.settings.components.PreferencesSection
import com.example.nufianapp.presentation.screens.settings.components.SettingHeader

@Composable
fun SettingActivity(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToChangePassword: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val userId = userViewModel.currentUserId ?: ""
    
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userViewModel.fetchUserById(userId)
        }
    }

    val userDataResponse by userViewModel.userByIdData.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xfff0f0f0))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .systemBarsPadding()
        ) {
            when (userDataResponse) {
                is Response.Loading -> {
                    ContentResponseLoading()
                }

                is Response.Success -> {
                    
                    val user = (userDataResponse as Response.Success<User?>).data
                    val avatarUrl = user?.avatarUrl ?: ""

                    SettingHeader(
                        navigateBack,
                        navigateToSignIn,
                        signOut = { profileViewModel.signOut() },
                        userViewModel = userViewModel
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = if (avatarUrl.isEmpty()) painterResource(id = R.drawable.img_avatar_default)
                                else rememberAsyncImagePainter(avatarUrl),
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .requiredSize(105.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(12.dp)) // Add spacing between image and text

                            user?.let {
                                Text(
                                    text = it.displayName,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = it.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    AccountSection(
                        navigateToEditProfile = navigateToEditProfile,
                        navigateToChangePassword = navigateToChangePassword
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    PreferencesSection()
                }

                is Response.Failure -> {
                    ContentResponseError(
                        message = "Failed to load account data. Please try again.",
                        navigateBack = navigateBack,
                        onRetry = { })
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun SettingActivityPreview() {
    SettingActivity(
        navigateBack = { },
        navigateToSignIn = { },
        navigateToEditProfile = { },
        navigateToChangePassword = { }
    )
}
