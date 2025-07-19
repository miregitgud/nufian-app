package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.data.model.User
import com.example.nufianapp.presentation.screens.profile.viewmodel.ProfileViewModel
import com.example.nufianapp.ui.theme.Red

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToDetail: (String, String, Boolean) -> Unit,
    navigateToSignIn: () -> Unit = {},
    navigateToDetailProject: (String, String) -> Unit,
    user: User?,
    isProfileView: Boolean = false,
    onPageChange: (Int) -> Unit = {},
    isProfile: Boolean = false
) {

    var isImageEnlarged by remember { mutableStateOf(false) }

    if (user != null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ProfileHeader(
                navigateBack = navigateBack,
                navigateToSetting = navigateToSetting,
                isProfileView = isProfileView
            )
            ProfileDetails(
                user,
                onImageClick = { isImageEnlarged = true }
            )
            PagerProfile(
                onPageChange = onPageChange,
                isProfile = isProfile,
                userId = user.uid,
                navigateToDetailProject = navigateToDetailProject,
                navigateToDetail = navigateToDetail
            )
        }
    } else {
        Text(text = "There might be something wrong, please try relogging the account.")
        Button(
            onClick = {
                profileViewModel.signOut()
                navigateToSignIn()

            },
            colors = ButtonDefaults.buttonColors(containerColor = Red.copy(alpha = 0.1f)),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 30.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Logout",
                color = Red
            )
        }
    }

    if (isImageEnlarged) {
        if (user != null) {
            user.avatarUrl?.let {
                EnlargedImageDialog(
                    onDismiss = { isImageEnlarged = false },
                    avatarUrl = it
                )
            }
        }
    }
}