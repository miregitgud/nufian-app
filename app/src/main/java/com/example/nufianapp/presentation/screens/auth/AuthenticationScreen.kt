package com.example.nufianapp.presentation.screens.auth

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.nufianapp.main.navigation.ScreenCustom
import com.example.nufianapp.ui.theme.DarkBlueGradient
import com.example.nufianapp.ui.theme.OrangeGradient
import kotlinx.coroutines.delay

@Composable
fun AuthenticationScreen(navController: NavController) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Login", "Sign up")
    val backgroundColor = if (tabIndex == 0) DarkBlueGradient else OrangeGradient
    var isLoginVisible by remember { mutableStateOf(true) }
    var isSignUpVisible by remember { mutableStateOf(false) }
    val slideOffsetRight = 1000
    val slideOffsetLeft = -1000
    var currentSlideOffset by remember { mutableStateOf(slideOffsetRight) }
    val context = LocalContext.current
    val activity = context as? Activity

    BackHandler {
        activity?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LaunchedEffect(tabIndex) {
            when (tabIndex) {
                0 -> {
                    if (!isLoginVisible) {
                        isSignUpVisible = false
                        currentSlideOffset = slideOffsetLeft
                        delay(100)
                        isLoginVisible = true
                    }
                }
                1 -> {
                    if (!isSignUpVisible) {
                        isLoginVisible = false
                        currentSlideOffset = slideOffsetRight
                        delay(100)
                        isSignUpVisible = true
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isLoginVisible && tabIndex == 0,
            enter = fadeIn(tween(300), initialAlpha = 1f),
            exit = fadeOut(tween(300))
        ) {
            LoginScreen(
                tabIndex = tabIndex,
                onTabSelected = { tabIndex = it },
                tabs = tabs,
                navController = navController,
                isVisible = isLoginVisible,
                slideOffset = currentSlideOffset
            )
        }
        AnimatedVisibility(
            visible = isSignUpVisible && tabIndex == 1,
            enter = fadeIn(tween(300), initialAlpha = 1f),
            exit = fadeOut(tween(300))
        ) {
            SignUpScreen(
                tabIndex = tabIndex,
                onTabSelected = { tabIndex = it },
                tabs = tabs,
                onNavigateToSetPassword = {
                    navController.navigate(ScreenCustom.SetPasswordScreenCustom.route)
                },
                isVisible = isSignUpVisible,
                slideOffset = currentSlideOffset
            )
        }
    }
}