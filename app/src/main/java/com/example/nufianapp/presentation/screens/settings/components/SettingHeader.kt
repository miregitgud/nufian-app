package com.example.nufianapp.presentation.screens.settings.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel

@Composable
fun SettingHeader(
    navigateBack: () -> Unit,
    navigateToSignIn: () -> Unit,
    signOut: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val userResponse by userViewModel.userByIdData.collectAsState()

    LaunchedEffect(userViewModel.currentUserId) {
        userViewModel.currentUserId?.let {
            userViewModel.fetchUserById(it)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ButtonIcon(onClickButton = navigateBack)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Settings",
            color = Color(0xff000000),
            style = TextStyle(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = { showDialog = true }) {
            Text("Logout", fontWeight = FontWeight.Bold, color = Color.Red)
        }
    }

    if (showDialog) {
        when (userResponse) {
            is Response.Loading -> {

            }

            is Response.Success -> {
                val user = (userResponse as Response.Success<User?>).data
                if (user != null) {
                    LogoutDialog(
                        userName = user.displayName,
                        onConfirm = {
                            showDialog = false
                            signOut()
                            navigateToSignIn()
                        },
                        onDismiss = { showDialog = false }
                    )
                }
            }

            is Response.Failure -> {
                // optional: show error message
                showDialog = false
            }

        }
    }
}
